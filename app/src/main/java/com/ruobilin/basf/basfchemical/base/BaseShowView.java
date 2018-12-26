package com.ruobilin.basf.basfchemical.base;

/**
 * Created by xingcc on 2018/12/26.
 * main function
 *
 * @author strivecheng
 */

public interface BaseShowView extends BaseView {
    void showLoading();

    void dismissLoading();

    void showError(String msg);
}
