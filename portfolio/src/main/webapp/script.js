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

/*
 * Returns a random fun fact about me from the /facts servlet.
 * 
 * REQUIRES: none
 * ENSURES: The "facts" container is replaced with a randomly chosen fun fact as
 *          defined in the facts array, and that fact is removed from facts. If
 *          the facts array is empty, the "generate-fact" container is removed.
 */
function getRandomFact() {
  fetch('/facts').then(response => response.text()).then((fact) => {
    document.getElementById('facts').innerText = fact;
  });
}

/*
 * Returns comments from the /comments servlet.
 *
 * REQUIRES: none
 * ENSURES: The "comments-container" div container is replaced with the comments
 *          stored in the "/comments" servlet as a comma-separated string. If the
 *          server returns an error, a nondescript error message is displayed instead.
 */
function getComments() {
  fetch('/comments')
    .then(handleFetchErrors)
    .then(response => response.json())
    .then(commentsInJson => {
      document.getElementById('comments-container').innerText = comments.toString();
  }).catch(error => {
      document.getElementById('facts').innerText = error;
  });
}

function handleFetchErrors(response) {
  if (!response.ok) {
    throw "Looks like something went wrong, you can try again.";
  }
  return response;
} 