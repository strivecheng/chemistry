package com.ruobilin.basf.basfchemical.base;

/**
 * Created by xingcc on 2018/12/11.
 * main function
 *
 * @author strivecheng
 */

public interface BaseView<T> extends BaseShowView{
    void setPresenter(T presenter);
}
