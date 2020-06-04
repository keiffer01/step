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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  // List of fun facts 
  private List<String> facts;
  // Pointer to the current index of facts given when the servlet is called
  private int currQuestionIndex;

  @Override
  public void init() {
    // Initializes currQuestionIndex and the facts list
    currQuestionIndex = 0;
    facts = new ArrayList<>();
    facts.add("Some of my friends call me by the nickname \"Puffball\" due to "
              + "my hair's tendency to get poofy.");
    facts.add("I'm confident that I've played at least 1 video game from every "
              + "Nintendo video game series.");
    facts.add("Despite being Filipino, I can barely speak any. :( "
              + "Trying to practice though so that one day...!");
    facts.add("Although I listen to Pop most of the time, my guilty pleasure "
               + "music are video game OSTs. Yoko Shimomura is just too good "
               + "of a composer.");
    facts.add("One of the career paths I once considered was being a writer.");
    
    Collections.shuffle(facts);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    /* When the pointer exceeds the size of the facts list (or somehow becomes
     * negative), reset currQuestionIndex and reshuffle the facts list. */
    if (currQuestionIndex >= facts.size() || currQuestionIndex < 0) {
      currQuestionIndex = 0;
      Collections.shuffle(facts);
    }

    String fact = facts.get(currQuestionIndex);
    currQuestionIndex++;

    response.setContentType("text/html;");
    response.getWriter().println(fact);
  }
}