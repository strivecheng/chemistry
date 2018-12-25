package com.ruobilin.basf.basfchemical.activity;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.ruobilin.basf.basfchemical.R;
import com.ruobilin.basf.basfchemical.base.BaseActivity;
import com.ruobilin.basf.basfchemical.common.Constant;

import java.io.File;

/**
 * Create by xingcc on 2018/12/10
 * main function:PDF文件预览的界面
 * @author  xingcc
 */
public class PDFPreviewActivity extends BaseActivity  {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String TAG = PDFPreviewActivity.class.getSimpleName();
    private PDFView mPDFView;
    private String path = "";
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private ImageView mBackImage;
    private TextView mPDFTitleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_pdfpreview;
    }

    @Override
    protected void initView() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            path = bundle.getString(Constant.PATH);
        }
        mPDFView = findViewById(R.id.pdf_view);
        mBackImage = findViewById(R.id.back_image);
        mPDFTitleTv =  findViewById(R.id.pdf_title_tv);
        //获取动态权限

    }

    @Override
    protected void initData() {
        File file = new File(path);
        mPDFTitleTv.setText(file.getName());
        displayFromFile(file);
    }

    private void displayFromFile(File file) {
        mPDFView.fromFile(file)
                .defaultPage(0)
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {

                    }
                })
                .enableAnnotationRendering(true)
                .onLoad(new OnLoadCompleteListener() {
                    @Override
                    public void loadComplete(int nbPages) {

                    }
                })
                .onError(new OnErrorListener() {
                    @Override
                    public void onError(Throwable t) {
                        Log.e(TAG, "onError: --"+ t.getMessage());
                    }
                })
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
    }

    @Override
    protected void initClick() {
        mBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
