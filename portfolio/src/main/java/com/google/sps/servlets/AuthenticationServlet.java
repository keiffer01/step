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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;

/** Servlet that authenticates users using Google's Users API */
@WebServlet("/authenticate")
public class AuthenticationServlet extends HttpServlet {
  private String LOGIN_MESSAGE = "<p>Looks like you're not logged in. To submit a comment, login "
      + "<a href=%s>here</a>.</p>";
  private String LOGOUT_MESSAGE =
      "<p>You're logged in as %s. Logout <a href=%s>here</a>.</p>";

  /**
   * {@inheritDoc}
   *
   * Authenticates the user and returns a corresponding message.
   *
   * This function is called each time comments.html is loaded to authenticate the user. If already
   * logged in, a logout link and corresponding message is shown. If not logged in, a login link and
   * corresponding message is shown.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    boolean isLoggedIn = userService.isUserLoggedIn();
    String message;

    // Create message based on whether the user is logged in or not
    String redirectUrl = "/comments.html";
    if (isLoggedIn) {
      String userEmail = userService.getCurrentUser().getEmail();
      String logoutUrl = userService.createLogoutURL(redirectUrl);
      message = String.format(LOGOUT_MESSAGE, userEmail, logoutUrl);
    } else {
      String loginUrl = userService.createLoginURL(redirectUrl);
      message = String.format(LOGIN_MESSAGE, loginUrl);
    }

    JSONObject authenticationResult = new JSONObject();
    try {
      authenticationResult.put("isLoggedIn", isLoggedIn);
      authenticationResult.put("message", message);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    response.setContentType("application/json;");
    response.getWriter().println(authenticationResult.toString());
  }
}
