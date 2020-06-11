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
      document.getElementById('comments-container').innerText =
        commentsInJson.toString();
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