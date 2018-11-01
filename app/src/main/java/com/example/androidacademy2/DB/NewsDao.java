package com.example.androidacademy2.DB;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


@Dao
public interface NewsDao {


    @Query("SELECt * FROM newsTable")
    List<NewsEntity> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(NewsEntity... newss);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(NewsEntity news);

    @Delete
    void delete(NewsEntity news);

    @Query("DELETE FROM newsTable")
    void deleteAll();

    @Query("SELECT * FROM newsTable WHERE title = :title LIMIT 1")
    NewsEntity findByName(String title);

    @Query("SELECT * FROM newsTable WHERE url = :id LIMIT 1")
    NewsEntity findById(String id);

    @Query("SELECT * FROM newsTable WHERE category = :cat")
    List<NewsEntity> loadAllByCategory(String cat);

}
