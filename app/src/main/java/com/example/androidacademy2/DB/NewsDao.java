package com.example.androidacademy2.DB;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Completable;
import io.reactivex.Observable;


@Dao
public interface NewsDao {


    @Query("SELECT * FROM newsTable")
    Observable<List<NewsEntity>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(NewsEntity... newss);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(NewsEntity news);

    @Query("UPDATE newsTable SET title = :x, previewText = :y, publishDate =:z WHERE url = :id")
    void updateById(String id, String x, String y, String z);

    @Delete
    void delete(NewsEntity news);

    @Query("DELETE FROM newsTable")
    void deleteAll();

    @Query("DELETE FROM newsTable WHERE url = :id")
    void deleteById(String id);

    @Query("SELECT * FROM newsTable WHERE title = :title LIMIT 1")
    Observable<NewsEntity> findByName(String title);

    @Query("SELECT * FROM newsTable WHERE url = :id LIMIT 1")
    Observable<NewsEntity> findById(String id);

    @Query("SELECT * FROM newsTable WHERE category = :cat")
    Observable<List<NewsEntity>> loadAllByCategory(String cat);

}
