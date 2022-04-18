package com.example.myapplication;

import com.example.myapplication.databaseRecords.ContentDatabaseRecord;

import java.io.File;
import java.util.List;

import me.xdrop.fuzzywuzzy.FuzzySearch;

/**
 * Represents both the text and non-text content of a {@link Post}.
 */
public class Content {

    /**
     * List of non-text attachments.
     */
    private List<File> attachments;

    private String attachment;

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
    public Content(String attachment, String title, String body) {
        this.attachment = attachment;
        this.title = title;
        this.body = body;
    }

    public int getSearchQueryScore(String query) {
        return FuzzySearch.weightedRatio(query, title) + FuzzySearch.weightedRatio(query, body);
    }

    public String getAttachment() {
        return attachment;
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
        return new Content(record.attachment, record.title, record.body);
    }
}
