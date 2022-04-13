package com.example.myapplication;

/**
 * Types of users.
 */
public enum UserType {
    /**
     * Normal logged-in user.
     */
    USER,

    /**
     * Moderator with access to removing questions, answers and replies.
     */
    MODERATOR,

    /**
     * Guest with no permissions to change, but only view. Effectively read-only.
     */
    GUEST
}
