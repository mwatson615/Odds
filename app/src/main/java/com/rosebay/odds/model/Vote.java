package com.rosebay.odds.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "votes")
public class Vote {

    @PrimaryKey(autoGenerate = true)
    private int uid;
    @ColumnInfo(name = "post_id")
    private String postId;
    @ColumnInfo(name = "username")
    private String username;
    @ColumnInfo(name = "voted_yes")
    private Boolean votedYes;

    public Vote() {}

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getVotedYes() {
        return votedYes;
    }

    public void setVotedYes(Boolean votedYes) {
        this.votedYes = votedYes;
    }

}
