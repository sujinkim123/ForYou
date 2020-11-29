package org.techtown.foryou.model;

import java.util.Calendar;

public class DateModel {

    private int year, month, day;

    public DateModel(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public void setTimeFromCalendar(Calendar calendar) {
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH);
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
