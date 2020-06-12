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

/**
 * Returns a random fun fact about me from the facts servlet, placing it in the
 * footer.
 */
function getRandomFact() {
  fetch("/facts").then(response => response.text()).then((fact) => {
    document.getElementById("facts").innerText = fact;
  });
}

/**
 * Determines if the user is logged in using the authenticate servlet.
 * Displays a login/logout link depending on the user status.
 */
function authenticateUser() {
  const authenticationContainer = document.getElementById("authenticate");
  const submitCommentForm = document.getElementById("submit-comment");

  fetch("/authenticate")
    .then(handleFetchErrors)
    .then(response => response.json())
    .then(authenticationResults => {
      authenticationContainer.innerHTML = authenticationResults.message;
      
      if (!authenticationResults.isLoggedIn) {
        submitCommentForm.classList.add("hidden");
      }
    })
    .catch(error => {
      // Proceed as if the user is not logged in
      submitCommentForm.classList.add("hidden");
      authenticationContainer.innerHTML = "<p>" + error + "</p>";
    });
}

/**
 * Returns comments from the comments servlet, placing each comment as a new
 * list element in an unordered list. On error, displays a generic error message
 * instead.
 */
function getComments() {
  fetch("/get-comments")
    .then(handleFetchErrors)
    .then(response => response.json())
    .then(commentsInJson => {
      const commentsList = document.getElementById("comments-list");
      commentsInJson.forEach(comment => {
        commentsList.appendChild(createCommentListItem(comment));
      })
    }).catch(error => {
      // Display generic error message in case of server error
      document.getElementById("comments-list").innerText = error;
  });
}

/**
 * Returns the response if its HTTP status code is successful as given by its
 * "ok" flag. If not, throws a generic error message.
 * 
 * @param {Response} response The HTTP response returned by the servlet.
 */
function handleFetchErrors(response) {
  if (!response.ok) {
    throw "Looks like something went wrong. Try refreshing the page and trying again.";
  }
  return response;
}

/**
 * Create a new list item element containing text of the given comment and a
 * button to delete it.
 * 
 * @param {JSON} comment JSON containing the comment fields.
 * @returns {HTMLLIElement} The list item to append to the comments list.
 */
function createCommentListItem(comment) {
  // Create the list item
  const listItem = document.createElement("li");
  listItem.className = "comment";

  // Create the comment text to put into the list item
  const listText = document.createElement("span");
  listText.innerText = `${nickname}: ${comment.text}\n` +
                       `Sentiment Analysis: ${comment.sentiment}`;
  listItem.appendChild(listText);

  // Create the delete button to put into the list item if the logged in user
  // owns the comment
  if (comment.isOwner) {
    const deleteButton = document.createElement("button");
    deleteButton.innerText = "Delete";
    deleteButton.addEventListener("click", () => {
      deleteComment(comment);
      listItem.remove();
    });
    listItem.appendChild(deleteButton);
  }

  return listItem;
}

/**
 * Deletes the given comment from the datastore.
 * 
 * @param {JSON} comment The comment to be deleted.
 */
function deleteComment(comment) {
  const params = new URLSearchParams();
  params.append("id", comment.id);
  fetch("/delete-comment", {method: "POST", body: params});
}