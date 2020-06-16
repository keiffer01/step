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
    throw "Looks like something went wrong, you can try again.";
  }
  return response;
}

/**
 * Create a new list item element containing text of the given comment and a
 * button to delete it.
 *
 * @param {JSON} comment JSON containing the comment id, text, and timestamp.
 * @returns {HTMLLIElement} The list item to append to the comments list.
 */
function createCommentListItem(comment) {
  // Create the list item
  const listItem = document.createElement("li");
  listItem.className = "comment";

  // Create the comment text to put into the list item
  const listText = document.createElement("span");
  listText.innerText = comment.text;

  // Create the delete button to put into the list item
  const deleteButton = document.createElement("button");
  deleteButton.innerText = "Delete";
  deleteButton.addEventListener("click", () => {
    deleteComment(comment);
    listItem.remove();
  });

  listItem.appendChild(listText);
  listItem.appendChild(deleteButton);
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

/** Creates a map with markers and adds it to the page. */
function createMap() {
  const myHometownPosition = {lat: 40.886, lng: -73.2573};
  const myCollegePosition = {lat: 40.443, lng: -79.943};

  const map = new google.maps.Map(document.getElementById('map'), {
    // Coordinates set to my hometown.
    center: myHometownPosition,
    zoom: 14,
    styles: makeNightMapStyle()
  });

  const hometownMarker = new google.maps.Marker({
    position: myHometownPosition,
    map: map,
    title: "My hometown"
  });
  const collegeMarker = new google.maps.Marker({
    position: myCollegePosition,
    map: map,
    title: "My college"
  });
}

/**
 * Returns a style object for Google Maps that sets it to night mode. Style
 * values given in the Maps JavaScript API example page for styling maps.
 *
 * @returns {MapTypeStyle Array} Style array for a Google Maps object.
 */
function makeNightMapStyle() {
  return [
    {elementType: 'geometry', stylers: [{color: '#242f3e'}]},
    {elementType: 'labels.text.stroke', stylers: [{color: '#242f3e'}]},
    {elementType: 'labels.text.fill', stylers: [{color: '#746855'}]},
    {
      featureType: 'administrative.locality',
      elementType: 'labels.text.fill',
      stylers: [{color: '#d59563'}]
    },
    {
      featureType: 'poi',
      elementType: 'labels.text.fill',
      stylers: [{color: '#d59563'}]
    },
    {
      featureType: 'poi.park',
      elementType: 'geometry',
      stylers: [{color: '#263c3f'}]
    },
    {
      featureType: 'poi.park',
      elementType: 'labels.text.fill',
      stylers: [{color: '#6b9a76'}]
    },
    {
      featureType: 'road',
      elementType: 'geometry',
      stylers: [{color: '#38414e'}]
    },
    {
      featureType: 'road',
      elementType: 'geometry.stroke',
      stylers: [{color: '#212a37'}]
    },
    {
      featureType: 'road',
      elementType: 'labels.text.fill',
      stylers: [{color: '#9ca5b3'}]
    },
    {
      featureType: 'road.highway',
      elementType: 'geometry',
      stylers: [{color: '#746855'}]
    },
    {
      featureType: 'road.highway',
      elementType: 'geometry.stroke',
      stylers: [{color: '#1f2835'}]
    },
    {
      featureType: 'road.highway',
      elementType: 'labels.text.fill',
      stylers: [{color: '#f3d19c'}]
    },
    {
      featureType: 'transit',
      elementType: 'geometry',
      stylers: [{color: '#2f3948'}]
    },
    {
      featureType: 'transit.station',
      elementType: 'labels.text.fill',
      stylers: [{color: '#d59563'}]
    },
    {
      featureType: 'water',
      elementType: 'geometry',
      stylers: [{color: '#17263c'}]
    },
    {
      featureType: 'water',
      elementType: 'labels.text.fill',
      stylers: [{color: '#515c6d'}]
    },
    {
      featureType: 'water',
      elementType: 'labels.text.stroke',
      stylers: [{color: '#17263c'}]
    }
  ];
}
