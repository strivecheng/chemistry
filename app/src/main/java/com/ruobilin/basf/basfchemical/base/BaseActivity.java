package com.ruobilin.basf.basfchemical.base;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.ruobilin.basf.basfchemical.common.widget.SJUITipDialog;

/**
 * Created by xingcc on 2018/12/3.
 * main function
 * 基类ac
 *
 * @author strivecheng
 */

public abstract class BaseActivity extends AppCompatActivity {

    private SJUITipDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bindLayout());
        initView();
        initData();
        initClick();
        // 保持Activity处于唤醒状态
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.BLACK);
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        }
    }


    /**
     * 需要绑定的布局
     *
     * @return
     */
    protected abstract int bindLayout();

    /**
     * 初始化view
     */
    protected abstract void initView();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 点击事件
     */
    protected abstract void initClick();

    /**
     * 不携带数据跳转
     *
     * @param cls
     */
    public void skipActivity(Class<?> cls) {
        skipActivity(cls, null);
    }

    /**
     * 携带数据跳转
     *
     * @param cls
     * @param bundle
     */
    public void skipActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    public void skipActivityForResult(Class<?> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    public void showProgressDialog(String message) {
        loadingDialog = new SJUITipDialog.Builder(this)
        .setIconType(SJUITipDialog.Builder.ICON_TYPE_LOADING)
        .setTipWord(message).create();
        loadingDialog.show();
    }

    public void hideProgressDialog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (this.isDestroyed()){
                return;
            }
        }
        if (loadingDialog!=null) {
            if (loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }
}
