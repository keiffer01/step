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

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  // Regex that accepts letters, numbers, and spaces
  private static final String regex = "^[A-Za-z0-9 ]*$";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) 
    throws IOException {
    String name = request.getParameter("name");
    response.setContentType("text/html;");

    if (name == null) {
        response.getWriter().println(
          "<h1>Oops, looks like you didn't give anything.</h1>");
    } else if (!name.matches(regex)){
        response.getWriter().println(
          "<h1>Sorry, I only accept letters, numbers, and spaces.</h1>");
    } else {
        response.getWriter().println("<h1>Hello " + name + "!</h1>");
    }
  }
}
