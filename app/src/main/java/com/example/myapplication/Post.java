package com.example.myapplication;

import android.text.Html;
import android.text.Spanned;

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

        long duration = new Date().getTime() - this.creationDate.getTime();

        long minutes = duration / 1000 / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long weeks = days / 7;

        if (minutes <= 0) {
            return "Just now";
        } else if (hours <= 0) {
            if (minutes == 1) {
                return minutes + " minute ago";
            } else {
                return minutes + " minutes ago";
            }
        } else if (days <= 0) {
            if (hours == 1) {
                return hours + " hour ago";
            } else {
                return hours + " hours ago";
            }
        } else if (weeks <= 0) {
            if (days == 1) {
                return days + " day ago";
            } else {
                return days + " days ago";
            }
        } else {
            SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy");
            return "on " + dtf.format(this.creationDate);
        }
    }

    public Spanned getAuthorAndDateText() {
        // TODO: Figure out how to make this color string not hardcoded
        // This will probably be done using spannable in the future

        String color = author.getUserType() == User.UserType.USER ?
                "#87ceeb" :
                "#178b17";

        return Html.fromHtml("<font color="+color+">"+this.getAuthor().getUserName()+"</font>"
                + " <font color=#a0a0a0>" + this.getCreationDateString() + "</font>");
    }

    /**
     * Get the author of the post.
     * @return the author
     */
    public User getAuthor() {
        return author;
    }
}
