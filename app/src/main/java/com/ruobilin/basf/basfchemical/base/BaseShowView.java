package com.ruobilin.basf.basfchemical.base;

/**
 * Created by xingcc on 2018/12/26.
 * main function
 *  view层基类接口
 * @author strivecheng
 */

public interface BaseShowView {
    void showLoading();

    void dismissLoading();

    void showError(String msg);
}
