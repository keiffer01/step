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
 * Returns a random fun fact about me until all facts are exhausted.
 */
var getRandomFact = (function() {
    const facts = ["hello", "world"];
    return function() {
        if (facts.length == 0) {
            const factContainer = document.getElementById("facts");
            const generatorContainer = document.getElementById("generate-fact");
            factContainer.innerText = "Empty!";
            generatorContainer.parentNode.removeChild(generatorContainer);
        }
        else {
            const fact = facts.splice(Math.floor(Math.random() * facts.length), 1);
            const factContainer = document.getElementById("facts");
            factContainer.innerText = fact;
        }
    }
})();

/*
 * Sticky navbar code - from W3Schools
 */
window.onscroll = function() {addOrRemoveSticky()};
var navbar = document.getElementById("navbar");
var sticky = navbar.offsetTop;

function addOrRemoveSticky() {
    if (window.pageYOffset >= sticky) {
        navbar.classList.add("sticky");
    } else {
        navbar.classList.remove("sticky");
    }
}