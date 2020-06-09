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
@WebServlet("/comments")
public class CommentsServlet extends HttpServlet {

  /** 
   * On GET request, writes to the response the comments list as a JSON string.
   * @param request The request made by the connecting client.
   * @param response The response that is sent back to the client.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Obtain and prepare comments from Datastore
    Query commentsQuery = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery commentsPrepared = datastore.prepare(commentsQuery);

    // Get maxComments query and check it is valid.
    int maxComments = getMaxComments(request);
    if (maxComments == -1) {
      response.setContentType("text/html");
      response.getWriter().println("Please enter an integer between 1 and 10.");
      return;
    }

    // Loop through each Comment entity until all comments are seen or until the max number of
    // comments have been reached, and store in an ArrayList.
    List<Comment> comments = new ArrayList<>();
    Iterator<Entity> commentsIterator = commentsPrepared.asIterable().iterator();
    int countComments = 0;
    Entity entity;
    while (commentsIterator.hasNext() && countComments < maxComments) {
      entity = commentsIterator.next();

      long id = entity.getKey().getId();
      String text = (String) entity.getProperty("text");
      long timestamp = (long) entity.getProperty("timestamp");

      Comment comment = new Comment(id, text, timestamp);
      comments.add(comment);

      countComments++;
    }

    // Convert comments to JSON using Gson
    String commentsInJson = new Gson().toJson(comments);

    // Send json as the response.
    response.setContentType("application/json;");
    response.getWriter().println(commentsInJson);
  }

  /** 
   * On POST request, stores given comment in the the datastore.
   * @param request The request made by the connecting client.
   * @param response The response that is sent back to the client.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String text = request.getParameter("comment-text");
    long timestamp = System.currentTimeMillis();

    // Do not store the comment if it the text is empty or null
    if (text == null || text.isEmpty()) {
      response.sendRedirect("/comments.html");
      return;
    }

    // Create new entity for the comment and store in Datastore
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("text", text);
    commentEntity.setProperty("timestamp", timestamp);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    response.sendRedirect("/comments.html");
  }

  /* Returns the value of maximum number of comments requested. */
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
}
