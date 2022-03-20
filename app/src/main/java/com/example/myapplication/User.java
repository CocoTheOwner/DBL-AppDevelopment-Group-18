package com.example.myapplication;

import androidx.annotation.NonNull;

import java.util.Random;

/**
 * Represents a user in the network.
 */
public class User {

    /**
     * Random generator for user IDs. TODO: Replace by Firebase IDs
     */
    private static final Random RANDOM = new Random();

    /**
     * The name of the user account.
     */
    private String username;

    /**
     * The major of the user.
     */
    private String program;

    /**
     * The user ID.
     */
    private String userID;

    /**
     * The type of the user account.
     */
    private UserType userType;

    /**
     * The email of the user.
     */
    private String email;

    /**
     * The password of the user.
     */
    private String userPassword;

    /**
     * Whether the account is disabled or not.
     */
    private boolean disabled = false;

    public User() {

    }

    /**
     * Login a User
     * @param userName      the name of the user
     * @param userMajor     the major of the user
     * @param userID        the ID to assign this user
     * @param userEmail     the email of the user
     * @param userPassword  the password
     */
    public User(String userName, String userMajor, String userID, UserType userType, String userEmail, String userPassword) {
        this.username = userName;
        this.program = userMajor;
        this.userID = userID;
        this.userType = userType;
        this.email = userEmail;
        this.userPassword = userPassword;
    }

    /**
     * Get whether the account is of the Guest type.
     * @return true if the account is a Guest account
     */
    public boolean isGuest() {
        return userType == UserType.GUEST;
    }

    /**
     * Get whether the account is disabled or not.
     * @return true if the account is disabled, else false
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * Disable the account.
     */
    public void disableAccount() {
        this.disabled = true;
    }

    /**
     * Enable the account after it has been disabled.
     */
    public void enableAccount() {
        this.disabled = false;
    }

    /**
     * Set the user's username.
     * @param userName the new username
     */
    public void setUserName(String userName) {
        this.username = userName;
    }

    /**
     * Get the user's username.
     * @return the username
     */
    public String getUserName() {
        return username;
    }

    /**
     * Get the user's email address.
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Resets the password of the user through the Firebase password reset sequence.
     */
    public void resetPassword() {

    }

    /**
     * Get a new ID for a user.
     * @return the new ID
     * TODO: Replace with firebase IDs
     */
    @NonNull
    public static String getNewUserID() {
        return String.valueOf(RANDOM.nextLong());
    }

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
}
