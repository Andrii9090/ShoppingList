package com.kasandco.familyfinance.app.finance.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.kasandco.familyfinance.core.BaseModel;
import com.kasandco.familyfinance.network.model.FinanceHistoryApiModel;

@Entity(tableName = "finance_history")
public class FinanceHistoryModel extends BaseModel {

    private String date;
    private String comment;
    @ColumnInfo(name = "category_id")
    private long categoryId;
    @ColumnInfo(name = "server_category_id")
    private long serverCategoryId;
    private double total;
    @ColumnInfo(defaultValue = "1")
    private int type;
    @ColumnInfo(name = "user_email", defaultValue = "")
    private String userEmail;

    public FinanceHistoryModel(){}

    @Ignore
    public FinanceHistoryModel(String date, long categoryId, long serverCategoryId, double total, String  comment, int type, String dateMod, String  userEmail){
        this.date = date;
        this.categoryId = categoryId;
        this.total = total;
        this.type = type;
        this.dateMod = dateMod;
        this.comment = comment;
        this.serverCategoryId = serverCategoryId;
        this.userEmail = userEmail;
    }
    public FinanceHistoryModel(FinanceHistoryApiModel responseModel){
        date = responseModel.getDate_create();
        serverCategoryId = responseModel.getFinance_category();
        total = responseModel.getTotal();
        type = responseModel.getType();
        dateMod = responseModel.getDate_mod();
        comment = responseModel.getComment();
        userEmail = responseModel.getEmail();
        dateModServer = responseModel.getDate_mod();
        serverId = responseModel.getId();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public long getServerCategoryId() {
        return serverCategoryId;
    }

    public void setServerCategoryId(long serverCategoryId) {
        this.serverCategoryId = serverCategoryId;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDateMod() {
        return dateMod;
    }

    public void setDateMod(String dateMod) {
        this.dateMod = dateMod;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment){
        this.comment = comment;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
