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
import com.google.appengine.api.datastore.FetchOptions;
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
   * {@inheritDoc}
   *
   * Returns the most recently posted comments.
   *
   * This servlet is called every time comments.html is loaded. The number of comments to send is
   * specified by {@code maxComments}.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();

    // Obtain and prepare comments from Datastore
    Query commentsQuery = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    List<Entity> commentsPrepared =
      datastore.prepare(commentsQuery).asList(FetchOptions.Builder.withLimit(maxComments));

    // Loop through each Comment entity until all comments are seen, storing them in an ArrayList
    List<Comment> comments = new ArrayList<>();
    for (Entity entity : commentsPrepared) {
      long id = entity.getKey().getId();
      String email = (String) entity.getProperty("email");
      String nickname = (String) entity.getProperty("nickname");
      String text = (String) entity.getProperty("text");
      float sentiment = getSentiment(text);
      boolean isOwner = userService.isUserLoggedIn() &&
                        email.equals(userService.getCurrentUser().getEmail());

      Comment comment = new Comment(id, nickname, text, sentiment, isOwner);
      comments.add(comment);
    }

    String commentsInJson = new Gson().toJson(comments);
    response.setContentType("application/json;");
    response.getWriter().println(commentsInJson);
  }

  /**
   * {@inheritDoc}
   *
   * Modifies the {@maxComments} static variable.
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
   * Returns the requested maximum number of comments to send, or -1 of the input is invalid.
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
