package com.example.juancastadmin.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Tools {
    public static String dateToString(Date date,String format)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(format);

        return  sdf.format(date);
    }public static String dateToString(Date date)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("M/d/y");

        return  sdf.format(date);
    }

    public static Date StringToDate(String dateString)
    {
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("M/d/y");
            return sdf.parse(dateString);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }public static Date StringToDate(String dateString,String format)
    {
        try{
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(dateString);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
