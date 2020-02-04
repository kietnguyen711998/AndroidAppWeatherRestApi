package com.example.appweather.Common;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {
    public static final String APP_ID= "fa1d5c57762f9305b2203cd547d2a6ec";
    public static Location current_location=null;

    public static String convertUnixToDate(long dt) {
        Date date= new Date(dt*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm EEE MM yyyy");
        String formatted = sdf.format(date);
        return formatted;
    }
    public static String convertUnixToHour(long dt) {
        Date date= new Date(dt*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String formatted = sdf.format(date);
        return formatted;
    }
}
