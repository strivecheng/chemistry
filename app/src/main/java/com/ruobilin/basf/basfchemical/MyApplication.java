package com.ruobilin.basf.basfchemical;

import android.app.Application;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.ruobilin.basf.basfchemical.common.Constant;
import com.ruobilin.basf.basfchemical.utils.LanguageUtils;

/**
 * Created by xingcc on 2018/12/3.
 * main function
 *
 * @author strivecheng
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sharedPreferences = getSharedPreferences(Constant.MY_SHARE_PREFERENCES, MODE_PRIVATE);
        String string = sharedPreferences.getString(Constant.LANGUAGE, "");
        if (TextUtils.isEmpty(string)){
            string = Constant.ENGLISH;
            sharedPreferences.edit().putString(Constant.LANGUAGE, string).apply();
        }
        LanguageUtils.changeAppLanguage(this,string);
    }
}
