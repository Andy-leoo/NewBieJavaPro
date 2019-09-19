package com.newbie.factory.utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;

public class DateTimeUtil {

    //使用 joda-time

    //str-date

    //date- str
    private static final  String STANDARO_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static Date strToDate(String str , String formatStr){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formatStr);
        DateTime dateTime = dateTimeFormatter.parseDateTime(str);
        return dateTime.toDate();
    }

    public static String dateToStr(Date date , String str){
        if (date == null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(str);
    }

    public static Date strToDate(String str ){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANDARO_FORMAT);
        DateTime dateTime = dateTimeFormatter.parseDateTime(str);
        return dateTime.toDate();
    }

    public static String dateToStr(Date date ){
        if (date == null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(STANDARO_FORMAT);
    }

}
