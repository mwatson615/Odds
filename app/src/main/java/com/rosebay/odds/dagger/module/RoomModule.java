package com.rosebay.odds.dagger.module;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.rosebay.odds.dagger.component.OddsApplicationScope;
import com.rosebay.odds.localStorage.AppDatabase;
import com.rosebay.odds.localStorage.FavoriteDao;
import com.rosebay.odds.localStorage.VoteDao;

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
