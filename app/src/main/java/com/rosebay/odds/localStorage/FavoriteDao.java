package com.rosebay.odds.localStorage;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.rosebay.odds.model.Favorite;

import java.util.List;

import io.reactivex.Maybe;

@Dao
public interface FavoriteDao {

    @Query("SELECT * FROM favorites")
    List<Favorite> getUserFavorites();

    @Query("SELECT post_id FROM favorites where post_id = :postId LIMIT 1")
    Maybe<String> findFavByPostID(String postId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long createFavorite(Favorite favorite);

    @Query("DELETE FROM favorites where post_id = :postId")
    int deleteFavorite(String postId);

}
