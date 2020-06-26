package com.rosebay.odds.localStorage;

import com.rosebay.odds.model.Vote;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Maybe;

@Dao
public interface VoteDao {

    @Query("SELECT post_id FROM votes where post_id = :postId LIMIT 1")
    Maybe<String> findVoteByPostID(String postId);

    @Insert
    Long createVoteEntry(Vote vote);
}
