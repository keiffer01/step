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

    /**
     * Returns the comment's id.
     * 
     * @return The comment id.
     */
    public long getId() {
        return id;
    }

    /**
     * Returns the comment's nickname.
     * 
     * @return The comment's nickname.
     */
    public String getNickname() {
        return nickname;
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
     * Returns if the logged in user is the owner of this comment.
     * 
     * @return True if the logged in user owns this comment.
     */
    public boolean getIsOwner() {
        return isOwner;
    }

    /**
     * Returns a string of the comment object's contents in a human-readable format.
     * 
     * @return A string containing the comment's contents.
     */
    public String toString() {
        return "ID: " + id + "," +
               "Nickname: " + nickname + "," +
               "Text: " + text + "," +
               "IsOwner: " + isOwner;
    }
}