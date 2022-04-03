package com.example.myapplication;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents a post in the app.
 */
public abstract class Post {

    /**
     * The ID of the post.
     */
    private final String postID;

    /**
     * The author of the post.
     */
    private final User author;

    /**
     * The content of the post.
     */
    private final Content content;

    /**
     * The timestamp on which the post was created.
     */
    private final Date creationDate;

    /**
     * Create a new post.
     * @param postID       the ID of the post
     * @param author       the author of the post
     * @param content      the content of the post
     * @param creationDate the date on which the post was created
     */
    protected Post(String postID, User author, Content content, Date creationDate) {
        this.postID = postID;
        this.author = author;
        this.content = content;
        this.creationDate = creationDate;
    }

    /**
     * Get the time since creation in a nicely formatted string.
     * @return the time since creation
     */
    public String timeDisplay(){
        // TODO: Proper format
        return creationDate.toString();
    }

    /**
     * Removes this post.
     * @return true if successful, false if not
     */
    public boolean remove() {
        // TODO: Implementation
        return true;
    }

    /**
     * Get the content of the post.
     * @return the content
     */
    public Content getContent() {
        return content;
    }

    /**
     * Get the ID of the post.
     * @return the ID
     */
    public String getPostID() {
        return postID;
    }

    /**
     * Get the creation date of the post.
     * @return the creation date
     */
    public Date getCreationDate() {
        return creationDate;
    }


    public String getCreationDateString() {
        SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy");
        return dtf.format(this.creationDate);
    }
    /**
     * Get the author of the post.
     * @return the author
     */
    public User getAuthor() {
        return author;
    }
}
