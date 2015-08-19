package com.example.kskhom.weather;

/**
 * Created by kskhom on 14.08.2015.
 */

public class Entry {
    public final String cloudiness;
    public final String temperature;
    public final String relwet;

    public final int day;
    public final int month;
    public final int year;

    Entry(String cloudiness, String temperature, String relwet, int day, int month, int year) {
        this.cloudiness = cloudiness;
        this.temperature = temperature;
        this.relwet = relwet;

        this.day = day;
        this.month = month;
        this.year = year;
    }
}