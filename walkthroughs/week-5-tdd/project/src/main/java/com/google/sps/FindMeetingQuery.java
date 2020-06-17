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

package com.google.sps;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

public final class FindMeetingQuery {

  /**
   * Returns all {@code TimeRange}s that satisfies the request constraints.
   *
   * @param events Collection of already scheduled {@code Event}s for the day.
   * @param request {@code MeetingRequest} containing all restraints for this query.
   * @return A Collection containing all {@code TimeRange}s that satisfies the constraints
   * specified by {@code request}.
   */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    ArrayList<TimeRange> availableTimes = new ArrayList<TimeRange>();

    // Get the availabilities of each attendee in the request.
    ArrayList<ArrayList<TimeRange>> attendeeAvailabilities = new ArrayList<ArrayList<TimeRange>>();
    for (String attendee : request.getAttendees()) {
      attendeeAvailabilities.add(getAttendeeAvailability(events, attendee));
    }

    // Get the intersection of the availabilities of every attendee in the request, with the entire
    // day as a base case.
    availableTimes.add(TimeRange.WHOLE_DAY);
    for (ArrayList<TimeRange> ranges : attendeeAvailabilities) {
      availableTimes = rangesIntersection(availableTimes, ranges);
    }

    removeTimesBelowDuration(availableTimes, request.getDuration());
    return availableTimes;
  }

  /**
   * Gets the available times in a day that the given attendee is available.
   *
   * @param events {@code Event} Collection for the day.
   * @param attendee Person who we wish to find the available times.
   * @return {@code TimeRange} Collection where the attendee is available.
   */
  private ArrayList<TimeRange> getAttendeeAvailability(Collection<Event> events, String attendee) {
    ArrayList<TimeRange> availableTimes = new ArrayList<TimeRange>();

    // Add TimeRanges over the entire day, skipping over the events that contains the attendee.
    int availableStart = TimeRange.START_OF_DAY;
    for (Event event : events) {
      if (event.getAttendees().contains(attendee)) {
        int eventStart = event.getWhen().start();
        int eventEnd = event.getWhen().end();

        availableTimes.add(TimeRange.fromStartEnd(availableStart, eventStart, false));
        availableStart = eventEnd;
      }
    }
    availableTimes.add(TimeRange.fromStartEnd(availableStart, TimeRange.END_OF_DAY, true));

    return availableTimes;
  }

  /**
   * Given two {@code TimeRange} ArrayLists, returns the intersection of the two interval lists.
   *
   * Example: |---| |--|
   *            |-----|
   * Returns:   |-| |-|
   *
   * @param arr1 The first ArrayList to intersect with.
   * @param arr2 The second ArrayList to intersect with.
   * @return The intersection of the two ArrayLists.
   */
  private ArrayList<TimeRange> rangesIntersection(ArrayList<TimeRange> arr1, ArrayList<TimeRange> arr2) {
    ArrayList<TimeRange> intersection = new ArrayList<TimeRange>();
    int arr1Index = 0, arr2Index = 0;

    // Iterating through the TimeRange endpoints of the ArrayLists, add the overlap between the
    // segments to the intersection.
    while (arr1Index < arr1.size() && arr2Index < arr2.size()) {
      TimeRange rangeFrom1 = arr1.get(arr1Index);
      TimeRange rangeFrom2 = arr2.get(arr2Index);

      if (rangeFrom1.overlaps(rangeFrom2)) {
        int start = Math.max(rangeFrom1.start(), rangeFrom2.start());
        int end = Math.min(rangeFrom1.end(), rangeFrom2.end());
        intersection.add(TimeRange.fromStartEnd(start, end, false));
      }

      if (rangeFrom1.end() < rangeFrom2.end()) {
        arr1Index++;
      } else {
        arr2Index++;
      }
    }

    return intersection;
  }

  /**
   * Removes the times in the given {@code ranges} that is below the given time duration.
   *
   * @param ranges The list of {@code TimeRange}s to consider.
   * @param duration The duration that all {@code TimeRange}s must not fall under.
   */
  private void removeTimesBelowDuration(ArrayList<TimeRange> ranges, long duration) {
    // Use iterator to avoid ConcurrentModificationException
    Iterator<TimeRange> iterator = ranges.iterator();
    while (iterator.hasNext()) {
      TimeRange range = iterator.next();
      if (range.duration() < duration) {
        iterator.remove();
      }
    }
  }
}
