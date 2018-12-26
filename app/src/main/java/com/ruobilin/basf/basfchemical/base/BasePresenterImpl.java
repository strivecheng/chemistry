package com.ruobilin.basf.basfchemical.base;

/**
 * Created by xingcc on 2018/12/26.
 * main function
 *
 * @author strivecheng
 */

public class BasePresenterImpl implements BaseRequestCallback{
    private BaseShowView baseShowView;

    public BasePresenterImpl(BaseShowView baseShowView) {
        this.baseShowView = baseShowView;
    }

    @Override
    public void onStart() {
        this.baseShowView.showLoading();
    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onError(Throwable throwable) {
        this.baseShowView.showError(throwable.getMessage());
    }

    @Override
    public void onComplete() {
        this.baseShowView.dismissLoading();
    }
}
