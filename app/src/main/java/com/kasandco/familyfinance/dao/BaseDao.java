package com.kasandco.familyfinance.dao;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

public interface BaseDao<T>{
    @Insert
    void insert(T obj);

    @Delete
    void delete(T obj);

    @Update
    void update(T obj);
}
