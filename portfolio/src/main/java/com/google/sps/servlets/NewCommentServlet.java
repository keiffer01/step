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
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 
 * Servlet that stores and returns comments.
 */
@WebServlet("/new-comment")
public class NewCommentServlet extends HttpServlet {

  /** 
   * On POST request, stores given comment in the the datastore.
   * @param request The request made by the connecting client.
   * @param response The response that is sent back to the client.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    
    String email = userService.getCurrentUser().getEmail();
    String nickname = request.getParameter("nickname");
    String text = request.getParameter("comment-text");
    long timestamp = System.currentTimeMillis();

    // Do not store the comment if it the text is empty or null
    if (text == null || text.isEmpty()) {
      response.sendRedirect("/comments.html");
      return;
    }

    // Set nickname to "Anonymous" if none specified
    if (nickname == null || nickname.isEmpty()) {
      nickname = "Anonymous";
    }

    // Create new entity for the comment and store in Datastore
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("email", email);
    commentEntity.setProperty("nickname", nickname);
    commentEntity.setProperty("text", text);
    commentEntity.setProperty("timestamp", timestamp);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    response.sendRedirect("/comments.html");
  }
}
