package com.ruobilin.basf.basfchemical.base;

/**
 * Created by xingcc on 2018/12/26.
 * main function
 * 基类数据接口
 * @author strivecheng
 */

public interface BaseRequestCallback {
    void onStart();

    void onSuccess();

    void onError(Throwable throwable);

    void onComplete();
}
