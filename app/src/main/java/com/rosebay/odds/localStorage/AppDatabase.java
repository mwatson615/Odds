package com.rosebay.odds.localStorage;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.rosebay.odds.model.Favorite;
import com.rosebay.odds.model.Vote;

@Database(entities = {Favorite.class, Vote.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract FavoriteDao getFavoriteDao();
    public abstract VoteDao getVoteDao();

}
