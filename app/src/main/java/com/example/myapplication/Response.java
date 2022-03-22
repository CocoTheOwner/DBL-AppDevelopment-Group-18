package com.example.myapplication;

import java.util.Date;

/**
 * Represents the response to a {@link Question}.
 */
public class Response extends InteractablePost {

    /**
     * Create a new post.
     *
     * @param postID       the ID of the post
     * @param author       the author of the post
     * @param content      the content of the post
     * @param creationDate the date on which the post was created
     */
    public Response(String postID, User author, Content content, Date creationDate) {
        super(postID, author, content, creationDate);
    }

    public static Response fromDatabaseRecord(String id, PostDatabaseRecord record, UserDatabaseRecord userRecord) {
        return new Response(id,
                User.fromDatabaseRecord(record.authorId, userRecord),
                Content.fromDatabaseRecord(record.content),
                record.creationDate);
    }
}
