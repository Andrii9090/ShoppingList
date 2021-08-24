package com.kasandco.familyfinance.service;

import androidx.room.Ignore;

import javax.inject.Inject;

public class Calculator {

    public static double addPl(double x, double y){
        return x + y;
    }
    public static double multiple(double x, double y){
        return x * y;
    }
    public static double divide(double x, double y){
        return x / y;
    }
    public static double subtract(double x, double y){
        return x - y;
    }
    public static double percent(double x, double y){
        return x * y/100;
    }
}
