package com.rosebay.odds.model;

import java.io.Serializable;

public class SingleOdd implements Serializable {

    private String postId;
    private String description;
    private String imageUrl;
    private String username;
    private int oddsFor;
    private int oddsAgainst;
    private int percentage;
    private String dateSubmitted;
    private String dueDate;

    public SingleOdd() {

    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public int getOddsFor() {
        return oddsFor;
    }

    public void setOddsFor(int oddsFor) {
        this.oddsFor = oddsFor;
    }

    public int getOddsAgainst() {
        return oddsAgainst;
    }

    public void setOddsAgainst(int oddsAgainst) {
        this.oddsAgainst = oddsAgainst;
    }

    public String getDateSubmitted() {
        return dateSubmitted;
    }

    public void setDateSubmitted(String dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
}
