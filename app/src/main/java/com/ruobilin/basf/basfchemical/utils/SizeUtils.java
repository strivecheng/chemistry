package com.ruobilin.basf.basfchemical.utils;

import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by xingcc on 2018/12/7.
 * main function
 * 尺寸转换
 * @author strivecheng
 */

public class SizeUtils {
    public static float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

}
