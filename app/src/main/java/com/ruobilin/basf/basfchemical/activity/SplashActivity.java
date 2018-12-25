package com.ruobilin.basf.basfchemical.activity;

import android.os.Bundle;
import android.os.Handler;

import com.ruobilin.basf.basfchemical.R;
import com.ruobilin.basf.basfchemical.base.BaseActivity;

/**
 * Create by xingcc on 2018/12/12
 * main function:启动页
 * @author  xingcc
 */
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                skipActivity(MainActivity.class);
                finish();
            }
        },2000);
    }

    @Override
    protected void initClick() {

    }
}
