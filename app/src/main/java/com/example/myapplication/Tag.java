package com.example.myapplication;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Represents a collection of tags.
 */
public class Tag {

    /*
     * Representation invariant:
     *
     * All elements in 'tags' are unique
     * All tags start with '#'
     */

    /**
     * List of tags currently present in the system.
     */
    private static final ArrayList<String> tags = new ArrayList<>();

    static {
        // TODO: Add tags on runtime from Firebase.
        // TODO: Optional: update tags when new ones appear on Firebase from 3rd parties.
    }

    /**
     * Add a tag to the tags list.
     * If the tag does not begin with '#', it is added.
     * @param name the name of the new tag
     * @return true if the tag is new, false if it exists
     */
    public static boolean add(@NonNull String name) {

        if (!name.startsWith("#")) {
            name = "#" + name;
        }

        String lower = name.toLowerCase(Locale.ENGLISH);
        for (String tag : tags) {
            if (tag.toLowerCase(Locale.ENGLISH).equals(lower)) {
                return false;
            }
        }
        tags.add(name);
        return true;
    }

    /**
     * Find similar tags.
     * @param query the query tag
     * @return the 5 closest tags to the specified query
     */
    @NonNull
    public static String[] findSimilarTags(String query) {
        // TODO: Implementation
        return tags.subList(0, 5).toArray(new String[]{});
    }
}
