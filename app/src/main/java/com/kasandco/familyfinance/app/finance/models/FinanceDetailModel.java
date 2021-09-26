package com.kasandco.familyfinance.app.finance.models;

import static com.kasandco.familyfinance.utils.DateHelper.formatDateToStr;
import static com.kasandco.familyfinance.utils.DateHelper.formatTimeToStr;

import androidx.room.ColumnInfo;

public class FinanceDetailModel {

    private String date;
    private String comment;
    @ColumnInfo(name = "category_id")
    private long categoryId;
    private double total;
    @ColumnInfo(defaultValue = "1")
    private int type;
    @ColumnInfo(name = "user_email")
    private String userEmail;
    private String time;

    public FinanceDetailModel(){}

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = formatDateToStr(date);
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public FinanceDetailModel(String _date, long _categoryId, double _total, String _comment, int _type){
        date = formatDateToStr(_date);
        time = formatTimeToStr(_date);
        categoryId = _categoryId;
        total = _total;
        type = _type;
        comment = _comment;
    }
    public FinanceDetailModel(String _date, int _type){
        date = formatDateToStr(_date);
        type = _type;
    }



    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
