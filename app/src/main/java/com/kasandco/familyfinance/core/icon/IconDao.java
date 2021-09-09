package com.kasandco.familyfinance.core.icon;

import androidx.room.Dao;
import androidx.room.Query;

import com.kasandco.familyfinance.dao.BaseDao;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface IconDao extends BaseDao<IconModel> {
    @Query("SELECT * FROM icon WHERE id=:id")
    IconModel getIcon(long id);

    @Query("SELECT * FROM icon")
    Single<List<IconModel>> getAllIcon();
}
