package com.rosebay.odds.localStorage;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.rosebay.odds.model.Vote;

import io.reactivex.Maybe;

@Dao
public interface VoteDao {

    @Query("SELECT post_id FROM votes where post_id = :postId LIMIT 1")
    Maybe<String> findVoteByPostID(String postId);

    @Insert
    Long createVoteEntry(Vote vote);
}
