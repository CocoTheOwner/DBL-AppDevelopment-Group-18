package com.example.myapplication;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class InteractablePost extends Post {

    /*
     * Representation Invariants:
     *
     * 'UpvotedBy' together 'DownvotedBy' can contain zero or one of each userID
     *
     * The replies in 'replies' are ordered by Date
     */

    /**
     * The replies to this post.
     */
    private final List<Reply> replies = new ArrayList<>();

    /**
     * The list of users that up-voted this post.
     */
    private final List<Long> upvotedBy = new ArrayList<>();

    /**
     * The list of users that down-voted this post.
     */
    private final List<Long> downvotedBy = new ArrayList<>();

    /**
     * Create a new post.
     *
     * @param postID       the ID of the post
     * @param author       the author of the post
     * @param content      the content of the post
     * @param creationDate the date on which the post was created
     */
    public InteractablePost(long postID, User author, Content content, Date creationDate) {
        super(postID, author, content, creationDate);
    }

    /**
     * Get the current vote score (upvotes - downvotes).
     * @return the current vote score
     */
    public int getVoteScore() {
        return upvotedBy.size() - downvotedBy.size();
    }

    public String getVoteScoreString() {
        return (this.getVoteScore() >= 0 ? "+" : "") + this.getVoteScore();
    }

    /**
     * Upvote this post.
     * @param userID the ID of the user that upvoted the post
     * @return true if the user did not already upvote the post
     */
    public boolean upvote(long userID) {
        if (upvotedBy.contains(userID)) {
            return false;
        }

        downvotedBy.remove(userID);

        upvotedBy.add(userID);
        return true;
    }


    /**
     * Downvote this post.
     * @param userID the ID of the user that downvoted the post
     * @return true if the user did not already downvote the post
     */
    public boolean downvote(long userID) {
        if (downvotedBy.contains(userID)) {
            return false;
        }

        upvotedBy.remove(userID);

        downvotedBy.add(userID);
        return true;
    }

    /**
     * Reply to this post.
     * TODO: Synchronize any replies added to prevent reply duplication
     * @param reply the reply to reply with
     * @return true if the reply was not already added
     */
    public boolean reply(Reply reply) {
        for (Reply _reply : replies) {
            if (_reply.getPostID() == reply.getPostID()) {
                return false;
            }
        }
        replies.add(reply);
        return true;
    }

    /**
     * Get the replies on this post.
     * @return the replies
     */
    public List<Reply> getReplies() {
        return replies;
    }
}
