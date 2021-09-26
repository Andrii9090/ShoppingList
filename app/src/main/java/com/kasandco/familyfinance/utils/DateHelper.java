package com.kasandco.familyfinance.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateHelper {

    @SuppressLint("SimpleDateFormat")
    public static String formatDateToStr(String date) {
        return new SimpleDateFormat("d.MM.yyyy").format(new Date(Long.parseLong(date)));
    }
    @SuppressLint("SimpleDateFormat")
    public static String formatTimeToStr(String date) {
        return new SimpleDateFormat("hh:mm").format(new Date(Long.parseLong(date)));
    }
    @SuppressLint("SimpleDateFormat")
    public static String formatDatePeriod(String pattern, long dateStart, long dateEnd) {
        return String.format(pattern, new SimpleDateFormat("dd/MM/yy").format(dateStart), new SimpleDateFormat("dd/MM/yy").format(dateEnd));
    }

    public static GregorianCalendar[] formatDateToStartAndEndDay(String dateStart, String dateEnd){
        GregorianCalendar start = new GregorianCalendar();
        start.setTime(new Date(Long.parseLong(dateStart)));
        start.set(start.get(Calendar.YEAR), start.get(Calendar.MONTH), start.get(Calendar.DATE), 0, 0, 0);

        Date endPeriod = new Date(Long.parseLong(dateEnd));
        GregorianCalendar end = new GregorianCalendar();
        end.setTime(endPeriod);
        end.set(end.get(Calendar.YEAR), end.get(Calendar.MONTH), end.get(Calendar.DATE), 23, 59, 59);
        return new GregorianCalendar[]{start,end};
    }
}
