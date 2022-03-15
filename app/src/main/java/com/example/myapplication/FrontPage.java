package com.example.myapplication;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the front page of the app.
 */
public class FrontPage {

    /*
     * Representation Invariant:
     *
     * All keys in 'featuredQuestions' are existing and registered tags.
     */

    /**
     * The questions currently featured on the home-page, grouped by {@link Tags}.
     */
    private final Map<String, Question[]> featuredQuestions = new HashMap<>();

}
