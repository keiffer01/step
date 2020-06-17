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
import java.util.Arrays;
import java.util.ArrayList;

public final class FindMeetingQuery {

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<String> attendees = request.getAttendees();
    ArrayList<Collection<TimeRange>> availabilities = new ArrayList<Collection<TimeRange>>();
    availabilities.add(Arrays.asList(TimeRange.WHOLE_DAY));

    for (String attendee : attendees) {
      availabilities.add(getAttendeeAvailability(events, attendee));
    }

    throw new UnsupportedOperationException("TODO: Implement this method.");
  }

  /**
   * Gets the available times in a day that the given attendee is available.
   *
   * @param events {@code Event} Collection for the day.
   * @param attendee Person who we wish to find the available times.
   * @return {@code TimeRange} Collection where the attendee is available.
   */
  private Collection<TimeRange> getAttendeeAvailability(Collection<Event> events, String attendee) {
    Collection<TimeRange> availableTimes = new ArrayList<TimeRange>();

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

  private Collection<TimeRange> intervalsIntersection(ArrayList<TimeRange> arr1, ArrayList<TimeRange> arr2) {
    Collection<TimeRange> intersection = new ArrayList<TimeRange>();
    int arr1Index = 0, arr2Index = 0;

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
}
