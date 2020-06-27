package com.rosebay.odds.dagger.module;

import android.content.Context;

import com.rosebay.odds.dagger.component.OddsApplicationScope;
import com.rosebay.odds.localStorage.AppDatabase;
import com.rosebay.odds.localStorage.FavoriteDao;
import com.rosebay.odds.localStorage.VoteDao;

import androidx.room.Room;
import dagger.Module;
import dagger.Provides;

@Module(includes = ContextModule.class)
public class RoomModule {

    @Provides
    @OddsApplicationScope
    AppDatabase providesRoomDatabase(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "app-database").build();
    }

    @Provides
    @OddsApplicationScope
    VoteDao providesVoteDao(AppDatabase appDatabase) {
        return appDatabase.getVoteDao();
    }

    @Provides
    @OddsApplicationScope
    FavoriteDao providesFavoritesDao(AppDatabase appDatabase) {
        return appDatabase.getFavoriteDao();
    }

}
