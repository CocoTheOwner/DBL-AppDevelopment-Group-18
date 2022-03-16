package com.example.myapplication;

import java.util.Date;

/**
 * A reply to a {@link InteractablePost}.
 */
public class Reply extends Post {
    /**
     * Create a new post.
     *
     * @param postID       the ID of the post
     * @param author       the author of the post
     * @param content      the content of the post
     * @param creationDate the date on which the post was created
     */
    public Reply(long postID, User author, Content content, Date creationDate) {
        super(postID, author, content, creationDate);
    }
}
