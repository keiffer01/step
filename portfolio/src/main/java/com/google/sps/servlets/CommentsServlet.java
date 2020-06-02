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

/** Servlet that stores comments. */
@WebServlet("/comments")
public class CommentsServlet extends HttpServlet {

  private List<String> comments;

  /* Initializes comments list with hard-coded data for now. */
  @Override
  public void init() {
    comments = new ArrayList<>();
    comments.add("Hello");
    comments.add("World");
  }

  /* Returns comments as a JSON string. */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Convert comments to JSON using Gson
    Gson gson = new Gson();
    String json = gson.toJson(comments);

    // Send json as the response
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }
}
