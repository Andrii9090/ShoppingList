package com.kasandco.shoplist.core.icon;

import androidx.room.Dao;
import androidx.room.Query;

import com.kasandco.shoplist.dao.BaseDao;

import java.util.List;

@Dao
public interface IconDao extends BaseDao<IconModel> {
    @Query("SELECT * FROM icon WHERE id=:id")
    IconModel getIcon(long id);

    @Query("SELECT * FROM icon")
    List<IconModel> getAllIcon();
}
