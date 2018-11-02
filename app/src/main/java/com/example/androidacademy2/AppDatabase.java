package com.example.androidacademy2;

import android.content.Context;

import com.example.androidacademy2.DB.NewsDao;
import com.example.androidacademy2.DB.NewsEntity;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {NewsEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase singleton;

    private static final String DATABASE_NAME = "DBNews.db";

    public abstract NewsDao newsDao();

    public static AppDatabase getAppDatabase(Context context) {
        if (singleton == null) {
            synchronized (AppDatabase.class) {
                if (singleton == null) {
                    singleton = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME)
                            //.allowMainThreadQueries()
                            .build();
                }
            }
        }
        return singleton;
    }

}

