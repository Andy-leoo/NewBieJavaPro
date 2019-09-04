package com.newbie.factory.utils;

import java.time.format.DateTimeFormatter;
import java.util.Date;

import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

public class DateTimeUtil {

    //使用 joda-time

    //str-date

    //date- str
    private static final  String STANDARO_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static Date strToDate(String str , String formatStr){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formatStr);
        DateTimeLiteralExpression.DateTime dateTime = dateTimeFormatter.parseDateTime(str);
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
