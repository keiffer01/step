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
import com.google.appengine.repackaged.com.google.api.client.util.Data;
import com.google.appengine.repackaged.com.google.api.client.util.store.DataStoreFactory;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 
 * Servlet that deletes comments from Datastore.
 */
@WebServlet("/delete-comments")
public class DeleteCommentsServlet extends HttpServlet {

  /**
   * On POST request, deletes all comments currently stored in the server's datastore.
   * @param request The request made by the connecting client.
   * @param response The response that is sent back to the client.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Obtain and prepare entities from datastore with kind "Comment".
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query commentsQuery = new Query("Comment");
    PreparedQuery commentsPrepared = datastore.prepare(commentsQuery);

    for (Entity entity : commentsPrepared.asIterable()) {
        datastore.delete(entity.getKey());
    }

    response.sendRedirect("/comments.html");
  }
}
