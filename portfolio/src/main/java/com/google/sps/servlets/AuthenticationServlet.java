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

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.json.Json;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that authenticates users using Google's Users API */
@WebServlet("/authenticate")
public class AuthenticationServlet extends HttpServlet {
  private String LOGIN_MESSAGE = "<p>Looks like you're not logged in. To submit a comment, login"
      + "<a href=\\\"%s\\\">here</a>.</p>";
  private String LOGOUT_MESSAGE =
      "<p>You're logged in as %s. Logout <a href=\\\"%s\\\">here</a>.</p>";

  /**
   * On GET request, returns true if the user is currently logged in, false otherwise. Also returns
   * a message to the user depending on whether they are logged in or not.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    boolean isLoggedIn = userService.isUserLoggedIn();
    String message;

    // Create message based on whether the user is logged in or not
    if (isLoggedIn) {
      String userEmail = userService.getCurrentUser().getEmail();
      String urlToRedirectToAfterUserLogsOut = "/comments.html";
      String logoutUrl = userService.createLogoutURL(urlToRedirectToAfterUserLogsOut);

      message = String.format(LOGOUT_MESSAGE, userEmail, logoutUrl);
    } else {
      String urlToRedirectToAfterUserLogsIn = "/comments.html";
      String loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);

      message = String.format(LOGIN_MESSAGE, loginUrl);
    }

    // Build json to send
    String json = Json.createObjectBuilder()
                      .add("isLoggedIn", isLoggedIn)
                      .add("message", message)
                      .build()
                      .toString();

    response.setContentType("application/json;");
    response.getWriter().println(json);
  }
}
