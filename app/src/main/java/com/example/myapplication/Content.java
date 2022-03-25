package com.example.myapplication;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.xdrop.fuzzywuzzy.FuzzySearch;

/**
 * Represents both the text and non-text content of a {@link Post}.
 */
public class Content {

    /**
     * List of non-text attachments.
     */
    private final List<File> attachments;

    /**
     * The title of the content.
     */
    private final String title;

    /**
     * The body of the content.
     */
    private final String body;

    /**
     * Create a new Content.
     * @param attachments   the attached files
     * @param title         the title
     * @param body          the body
     */
    public Content(@NonNull List<File> attachments, String title, String body) {
        this.attachments = attachments;
        this.title = title;
        this.body = body;
    }

    public int getSearchQueryScore(String query) {
        return FuzzySearch.weightedRatio(query, title) + FuzzySearch.weightedRatio(query, body);
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public List<File> getAttachments() {
        return attachments;
    }

    public static Content fromDatabaseRecord(ContentDatabaseRecord record) {
        return new Content(new ArrayList<>(), record.title, record.body);
    }
}
