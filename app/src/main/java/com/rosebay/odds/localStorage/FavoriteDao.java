package com.rosebay.odds.localStorage;

import com.rosebay.odds.model.Favorite;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
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
