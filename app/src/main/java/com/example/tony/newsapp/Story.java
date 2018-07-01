package com.example.tony.newsapp;

/**
 * Created by tony on 21.10.17.
 */

public class Story {
    private String storyTitle;
    private String storyUrl;
    private String storySectionName;
    private String storyPublicationDate;
    private String storyAuthor;

    public Story(String title, String section, String date, String url, String author) {
        storyTitle = title;
        storySectionName = section;
        storyPublicationDate = date;
        storyUrl = url;
        storyAuthor = author;
    }


    //Generate the getter methods for the class variabls.
    public String getStoryTitle() {
        return storyTitle;
    }
    public String getStoryUrl() {
        return storyUrl;
    }
    public String getStorySectionName() {
        return storySectionName;
    }
    public String getStoryPublicationDate() {
        return storyPublicationDate;
    }
    public String getStoryAuthor(){return  storyAuthor;}

    //These are the setter methods for the class variables.
    public void setStoryTitle(String title) {
        storyTitle = title;
    }
    public void setStoryUrl(String url) {
        storyUrl = url;
    }
    public void setStorySectionName(String name) {
        storySectionName = name;
    }
    public void setStoryPublicationDate(String publicationDate) {
        storyPublicationDate = publicationDate;
    }
    public void setStoryAuthor(String author){
        storyAuthor = author;
    }
}
