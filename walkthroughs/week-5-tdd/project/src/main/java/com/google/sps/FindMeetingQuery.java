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
import java.util.Collections;
import java.util.ArrayList;

public final class FindMeetingQuery {
  /**
   * Returns all {@code TimeRange}s that satisfies the request constraints.
   *
   * Has O(m*max(n, log m)) time complexity, where n is the number of attendees in the request and m
   * is the size of the {@code events} parameter.
   *
   * @param events Collection of already scheduled {@code Event}s for the day.
   * @param request {@code MeetingRequest} containing all restraints for this query.
   * @return A Collection containing all {@code TimeRange}s that satisfies the constraints
   * specified by {@code request}.
   */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    ArrayList<Event> sortedEvents = new ArrayList<Event>(events);
    Collections.sort(sortedEvents, Event.ORDER_BY_START_TIME);

    ArrayList<ArrayList<TimeRange>> attendeeAvailabilities =
        getAllAttendeeAvailabilities(sortedEvents, request.getAttendees());
    ArrayList<ArrayList<TimeRange>> optionalAttendeeAvailabilities =
        getAllAttendeeAvailabilities(sortedEvents, request.getOptionalAttendees());

    // Whole day TimeRange is a base case because it is the identity element, and should also be
    // returned when no attendees are given.
    ArrayList<TimeRange> base = new ArrayList<TimeRange>();
    base.add(TimeRange.WHOLE_DAY);
    ArrayList<TimeRange> availableTimesWithoutOptional =
        allTimeRangesIntersection(attendeeAvailabilities, base);
    ArrayList<TimeRange> availableTimesWithOptional =
        allTimeRangesIntersection(optionalAttendeeAvailabilities, availableTimesWithoutOptional);

    availableTimesWithoutOptional =
        removeTimesBelowDuration(availableTimesWithoutOptional, request.getDuration());
    availableTimesWithOptional =
        removeTimesBelowDuration(availableTimesWithOptional, request.getDuration());

    // We must also check that mandatory attendees is not empty. If there are only optional
    // attendees, we do not wish to accidentally return the entire day since it is the base case.
    return availableTimesWithOptional.isEmpty() && !request.getAttendees().isEmpty()
        ? availableTimesWithoutOptional
        : availableTimesWithOptional;
  }

  /**
   * Gets a list of all the available times that each attendee in attendees is available in the day.
   *
   * @param events {@code Event} Collection for the day.
   * @param attendees The collection of attendees which we wish to find all the availabilities of.
   * @return An ArrayList where each element is the list of times that a unique attendee is
   *         available.
   */
  private ArrayList<ArrayList<TimeRange>> getAllAttendeeAvailabilities(
      Collection<Event> events, Collection<String> attendees) {
    ArrayList<ArrayList<TimeRange>> attendeeAvailabilities = new ArrayList<ArrayList<TimeRange>>();
    for (String attendee : attendees) {
      attendeeAvailabilities.add(getAttendeeAvailability(events, attendee));
    }
    return attendeeAvailabilities;
  }

  /**
   * Gets the available times in a day that the given attendee is available.
   *
   * REQUIRES: The events given must be sorted by time.
   *
   * @param events {@code Event} Collection for the day.
   * @param attendee Person who we wish to find the available times.
   * @return {@code TimeRange} ArrayList where the attendee is available.
   */
  private ArrayList<TimeRange> getAttendeeAvailability(Collection<Event> events, String attendee) {
    ArrayList<TimeRange> availableTimes = new ArrayList<TimeRange>();

    // Starting from the beginning of the day, add TimeRanges where the attendee is available. Skip
    // over the events accordingly where the attendee is listed as attending.
    int availableStart = TimeRange.START_OF_DAY;
    for (Event event : events) {
      if (event.getAttendees().contains(attendee)) {
        int eventStart = event.getWhen().start();
        int eventEnd = event.getWhen().end();

        // Case where attendee has partially overlapping events:
        //     |-----|
        //        |-----|
        if (event.getWhen().contains(availableStart)) {
          availableStart = eventEnd;

          // Case where attendee has completely overlapping events:
          //     |------|
          //       |--|
        } else if (eventStart < availableStart) {
          // Skip

        } else {
          availableTimes.add(
              TimeRange.fromStartEnd(availableStart, eventStart, /*inclusiveEnd=*/false));
          availableStart = eventEnd;
        }
      }
    }
    availableTimes.add(
        TimeRange.fromStartEnd(availableStart, TimeRange.END_OF_DAY, /*inclusiveEnd=*/true));

    return availableTimes;
  }

  /**
   * Removes the times in the given {@code ranges} parameter that is below the given time duration.
   *
   * @param ranges The list of {@code TimeRange}s to consider.
   * @param duration The duration that all {@code TimeRange}s must not fall under.
   */
  private ArrayList<TimeRange> removeTimesBelowDuration(
      ArrayList<TimeRange> ranges, long duration) {
    ArrayList<TimeRange> timeRangesAboveDuration = new ArrayList<TimeRange>();
    for (TimeRange range : ranges) {
      if (range.duration() >= duration) {
        timeRangesAboveDuration.add(range);
      }
    }
    return timeRangesAboveDuration;
  }
}
