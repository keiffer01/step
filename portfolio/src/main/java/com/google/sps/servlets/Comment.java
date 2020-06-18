package com.google.sps.servlets;

/**
 * The Comment class contains the components of a "comment" object.
 * Namely, a unique id, the text content, and a timestamp.
 */
public class Comment {
  private long id;
  private String text;
  private long timestamp;

  /**
   * Comment constructor.
   *
   * @param id The unique identifier of this comment.
   * @param text The text content of this comment.
   * @param timestamp The time, in milliseconds, that this comment was submitted.
   */
  public Comment(long id, String text, long timestamp) {
    this.id = id;
    this.text = text;
    this.timestamp = timestamp;
  }
}
