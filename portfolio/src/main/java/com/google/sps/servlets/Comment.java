package com.google.sps.servlets;

/**
 * The Comment class contains the components of a "comment" object.
 * Namely, a unique id, the text content, and a timestamp.
 */
public class Comment {
  private long id;
  private String nickname;
  private String text;
  private boolean isOwner;

  /**
   * Comment constructor.
   *
   * @param id The unique identifier of this comment.
   * @param nickname Nickname associated with the comment.
   * @param text The text content of this comment.
   * @param isOwner True if the logged in user wrote this comment.
   */
  public Comment(long id, String nickname, String text, boolean isOwner) {
    this.id = id;
    this.nickname = nickname;
    this.text = text;
    this.isOwner = isOwner;
  }
}
