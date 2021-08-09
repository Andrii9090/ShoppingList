package com.kasandco.familyfinance.app.icon;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "icon")
public class IconModel {
    public IconModel(){

    }
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String path;
    @ColumnInfo(name = "text_search")
    public String textSearch;

    @Ignore
    public IconModel(String path, String textSearch){
        this.path = path;
        this.textSearch = textSearch;
    }
}
