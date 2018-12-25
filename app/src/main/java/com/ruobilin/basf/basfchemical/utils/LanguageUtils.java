package com.ruobilin.basf.basfchemical.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.ruobilin.basf.basfchemical.common.Constant;

import java.util.Locale;

/**
 * Created by xingcc on 2018/12/6.
 * main function
 * 语言切换的工具类
 *
 * @author strivecheng
 */

public class LanguageUtils {

    /**
     * 切换app的语言
     * @param context
     * @param language
     */
    public static void changeAppLanguage(Context context, String language) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            if (language.equals(Constant.SIMPLIFIED_CHINESE)) {
//                configuration.setLocale(Locale.SIMPLIFIED_CHINESE);
//            } else if (language.equals(Constant.ENGLISH)) {
//                configuration.setLocale(Locale.ENGLISH);
//            } else {
//                configuration.setLocale(Locale.getDefault());
//            }
//            context.createConfigurationContext(configuration);
//        } else {
            if (language.equals(Constant.SIMPLIFIED_CHINESE)) {
                configuration.locale = Locale.SIMPLIFIED_CHINESE;
            } else if (language.equals(Constant.ENGLISH)) {
                configuration.locale = Locale.ENGLISH;
            } else {
                configuration.locale = Locale.getDefault();
            }
            resources.updateConfiguration(configuration, metrics);
//        }


        }
    }
