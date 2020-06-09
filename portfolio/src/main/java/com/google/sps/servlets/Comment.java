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

    /**
     * Returns the comment's id.
     * 
     * @return The comment id.
     */
    public long getId() {
        return id;
    }

    /**
     * Returns the comment's text content.
     * 
     * @return The comment's text content.
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the comment's timestamp.
     * 
     * @return The comment's timestamp.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Returns a string of the comment object's contents in a human-readable format.
     * 
     * @return A string containing the comment's contents.
     */
    public String toString() {
        return id + ": " + text + " " + timestamp;
    }
}