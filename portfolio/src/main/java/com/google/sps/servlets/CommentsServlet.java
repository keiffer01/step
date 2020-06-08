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
@WebServlet("/comments")
public class CommentsServlet extends HttpServlet {

  private List<String> comments = new ArrayList<>();

  /** 
   * On GET request, writes to the response the comments list as a JSON string.
   * @param request The request made by the connecting client.
   * @param response The response that is sent back to the client.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException {
    // Convert comments to JSON using Gson.
    String commentsInJson = new Gson().toJson(comments);

    // Send json as the response.
    response.setContentType("application/json;");
    response.getWriter().println(commentsInJson);
  }

  /** 
   * On POST request, stores given comment in the comments ArrayList.
   * @param request The request made by the connecting client.
   * @param response The response that is sent back to the client.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException {
      String comment = request.getParameter("comment-input");
      if (comment != null && !comment.isEmpty()) {
        comments.add(comment);
      }

      // Redirect back to comments.html.
      response.sendRedirect("/comments.html");
    }
}
