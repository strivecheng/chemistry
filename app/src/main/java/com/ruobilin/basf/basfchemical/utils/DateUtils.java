package com.ruobilin.basf.basfchemical.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by xingcc on 2018/12/6.
 * main function
 * 日期工具类
 *
 * @author strivecheng
 */

public class DateUtils {
    public static final SimpleDateFormat DEFAULT_SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public static String getCurrentDateForYMD(Date date) {
        return DEFAULT_SDF.format(date);
    }
}
