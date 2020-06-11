// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 
 * Servlet that stores and returns comments.
 */
@WebServlet("/get-comments")
public class GetCommentsServlet extends HttpServlet {

  // The max number of comments to send on a GET request. Is modified on POST request.
  private int maxComments = 5;
  private static final String COMMENT = "Comment";

  /** 
   * On GET request, writes to the response the comments list as a JSON string.
   * @param request The request made by the connecting client.
   * @param response The response that is sent back to the client.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();

    // Obtain and prepare comments from Datastore
    Query commentsQuery = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery commentsPrepared = datastore.prepare(commentsQuery);

    // Loop through each Comment entity until all comments are seen or until the max number of
    // comments have been reached, and store in an ArrayList.
    List<Comment> comments = new ArrayList<>();
    Iterator<Entity> commentsIterator = commentsPrepared.asIterable().iterator();
    int countComments = 0;
    Entity entity;
    while (commentsIterator.hasNext() && countComments < maxComments) {
      entity = commentsIterator.next();

      long id = entity.getKey().getId();
      String email = (String) entity.getProperty("email");
      String nickname = (String) entity.getProperty("nickname");
      String text = (String) entity.getProperty("text");

      // Determine if the logged in user is the owner of this comment;
      boolean isOwner;
      if (userService.isUserLoggedIn()) {
        isOwner = email.equals(userService.getCurrentUser().getEmail());
      } else {
        isOwner = false;
      }

      Comment comment = new Comment(id, nickname, text, isOwner);
      comments.add(comment);

      countComments++;
    }

    // Convert comments to JSON using Gson.
    String commentsInJson = new Gson().toJson(comments);

    // Send json as the response.
    response.setContentType("application/json;");
    response.getWriter().println(commentsInJson);
  }

  /** 
   * On POST request, modifies the maximum number of comments to send.
   * @param request The request made by the connecting client.
   * @param response The response that is sent back to the client.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    int maxCommentsRequest = getMaxComments(request);
    if (maxCommentsRequest == -1) {
      return;
    }

    maxComments = maxCommentsRequest;
    response.sendRedirect("/comments.html");
  }

  /**
   * Returns the requested maximum number of comments to send as given by the POST request.
   * 
   * @param request The POST request containing the requested maximum number of comments to send.
   * @return The maximum number of comments to send.
   */
  private int getMaxComments(HttpServletRequest request) {
    String maxCommentsString = request.getParameter("max-comments");

    // Convert input to int.
    int maxComments;
    try {
      maxComments = Integer.parseInt(maxCommentsString);
    } catch (NumberFormatException e) {
      System.err.println("Could not convert to int: " + maxCommentsString);
      return -1;
    }

    // Check that the input is between 1 and 10
    if (maxComments < 1 || maxComments > 10) {
      System.err.println("Max comments is out of range: " + maxCommentsString);
      return -1;
    }

    return maxComments;
  }

  /**
   * Determines the positivity/negativity of comment text using the Cloud Natural Language library.
   * 
   * @param text The text to analyze the sentiment of.
   * @return A value between -1 and 1, representing how negative or positive the text is.
   * @throws IOException On failure to create LanguageServiceClient
   */
  private float getSentiment(String text) throws IOException {
    Document doc = Document.newBuilder().setContent(text).setType(Document.Type.PLAIN_TEXT).build();
    LanguageServiceClient languageService = LanguageServiceClient.create();
    Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
    float score = sentiment.getScore();

    languageService.close();
    return score;
  }
}
