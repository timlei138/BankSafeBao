package com.android.bsb.util;

import java.util.Calendar;
import java.util.Date;

public class TimeUtils {


    public static String getTime(long timemillis){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timemillis);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayofMonth = calendar.get(Calendar.DAY_OF_MONTH);
        AppLogger.LOGD("demo","year:"+year+",month:"+month+",dayOfMonth:"+dayofMonth);
        return year+":"+month+":"+dayofMonth;

    }

}
