package com.ruobilin.basf.basfchemical.activity;

import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.ruobilin.basf.basfchemical.R;
import com.ruobilin.basf.basfchemical.base.BaseActivity;
import com.ruobilin.basf.basfchemical.bean.ChemicalInfo;
import com.ruobilin.basf.basfchemical.bean.FileInfo;
import com.ruobilin.basf.basfchemical.common.Constant;
import com.ruobilin.basf.basfchemical.contract.ScanSearchChemicalContract;
import com.ruobilin.basf.basfchemical.dao.AbstractMyChemicalDataBase;
import com.ruobilin.basf.basfchemical.dao.ChemicalDao;
import com.ruobilin.basf.basfchemical.dao.FileDao;
import com.ruobilin.basf.basfchemical.model.ChemicalModelImpl;
import com.ruobilin.basf.basfchemical.presenter.ScanSearchChemicalPresenter;
import com.ruobilin.basf.basfchemical.zxinglibrary.android.BeepManager;
import com.ruobilin.basf.basfchemical.zxinglibrary.android.CaptureActivityHandler;
import com.ruobilin.basf.basfchemical.zxinglibrary.android.InactivityTimer;
import com.ruobilin.basf.basfchemical.zxinglibrary.bean.ZxingConfig;
import com.ruobilin.basf.basfchemical.zxinglibrary.camera.CameraManager;
import com.ruobilin.basf.basfchemical.zxinglibrary.view.ViewfinderView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * Create by xingcc on 2018/12/3
 * main function: 二维码扫描界面
 *
 * @author xingcc
 */
public class ScanActivity extends BaseActivity implements View.OnClickListener, SurfaceHolder
        .Callback, ScanSearchChemicalContract.View {//implements QRCodeView.Delegate, View
    // .OnClickListener


    private static final String TAG = ScanActivity.class.getSimpleName();
    public ZxingConfig config;
    private SurfaceView previewView;
    private ViewfinderView viewfinderView;
    private AppCompatImageView flashLightIv;
    private TextView flashLightTv;
    private AppCompatImageView backIv;
    private LinearLayoutCompat flashLightLayout;
    private LinearLayoutCompat bottomLayout;
    private boolean hasSurface;
    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;
    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private SurfaceHolder surfaceHolder;
    private ChemicalDao chemicalDao;
    private FileDao fileDao;
    private ScanSearchChemicalPresenter scanSearchChemicalPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_scan;
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }


    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void initView() {
        chemicalDao = AbstractMyChemicalDataBase
                .getInstance(this)
                .getChemicalDao();
        fileDao = AbstractMyChemicalDataBase.getInstance(this).getFileDao();

         /*先获取配置信息*/
        try {
            config = (ZxingConfig) getIntent().getExtras().get(com.ruobilin.basf.basfchemical
                    .zxinglibrary.common.Constant.INTENT_ZXING_CONFIG);
        } catch (Exception e) {
            Log.i("config", e.toString());
        }

        if (config == null) {
            config = new ZxingConfig();
        }

        previewView = findViewById(R.id.preview_view);
        previewView.setOnClickListener(this);

        viewfinderView = findViewById(R.id.viewfinder_view);
        viewfinderView.setZxingConfig(config);


        backIv = findViewById(R.id.backIv);
        backIv.setOnClickListener(this);

        flashLightIv = findViewById(R.id.flashLightIv);
        flashLightTv = findViewById(R.id.flashLightTv);

        flashLightLayout = findViewById(R.id.flashLightLayout);
        flashLightLayout.setOnClickListener(this);
        bottomLayout = findViewById(R.id.bottomLayout);


        switchVisibility(bottomLayout, config.isShowbottomLayout());
        switchVisibility(flashLightLayout, config.isShowFlashLight());


        /*有闪光灯就显示手电筒按钮  否则不显示*/
        if (isSupportCameraLedFlash(getPackageManager())) {
            flashLightLayout.setVisibility(View.VISIBLE);
        } else {
            flashLightLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initData() {
        scanSearchChemicalPresenter = new ScanSearchChemicalPresenter(ChemicalModelImpl
                .getInstance(this), this);

        hasSurface = false;

        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);
        beepManager.setPlayBeep(config.isPlayBeep());
        beepManager.setVibrate(config.isShake());
    }

    @Override
    protected void initClick() {

    }

    @Override
    protected void onStart() {
        super.onStart();
//        mZBarView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
//        mZBarView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT); // 打开前置摄像头开始预览，但是并未开始识别

//        mZBarView.startSpotAndShowRect(); // 显示扫描框，并且延迟0.1秒后开始识别
    }

    @Override
    protected void onResume() {
        super.onResume();

        cameraManager = new CameraManager(getApplication(), config);

        viewfinderView.setCameraManager(cameraManager);
        handler = null;

        surfaceHolder = previewView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            // 重置callback，等待surfaceCreated()来初始化camera
            surfaceHolder.addCallback(this);
        }

        beepManager.updatePrefs();
        inactivityTimer.onResume();

    }


    @Override
    protected void onStop() {
//        mZBarView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
        super.onStop();
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        beepManager.close();
        cameraManager.closeDriver();

        if (!hasSurface) {
            surfaceHolder.removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }


    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    //    @Override
    public void onScanQRCodeSuccess(final String result) {
        Log.i(TAG, "result:" + result);
        vibrate();
//        mZBarView.startSpot(); // 延迟0.1秒后开始识别
        String code = "";
        int inventory = 0;
        try {
            JSONObject jsonObject = new JSONObject(result);
            code = jsonObject.getString(Constant.CODE);
            if (jsonObject.has(Constant.INVENTORY)) {
                inventory = jsonObject.getInt(Constant.INVENTORY);
            }
        } catch (JSONException e) {
            Toast.makeText(this, R.string.error_format, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return;
        }

        scanSearchChemicalPresenter.searchChemicalByCode(code, inventory);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backIv:
                finish();
                break;
            case R.id.flashLightLayout:
                /*切换闪光灯*/
                cameraManager.switchFlashLight(handler);
                break;
            default:
        }
    }


    /**
     * @param pm
     * @return 是否有闪光灯
     */
    public static boolean isSupportCameraLedFlash(PackageManager pm) {
        if (pm != null) {
            FeatureInfo[] features = pm.getSystemAvailableFeatures();
            if (features != null) {
                for (FeatureInfo f : features) {
                    if (f != null && PackageManager.FEATURE_CAMERA_FLASH.equals(f.name)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * @param flashState 切换闪光灯图片
     */
    public void switchFlashImg(int flashState) {

        if (flashState == com.ruobilin.basf.basfchemical.zxinglibrary.common.Constant.FLASH_OPEN) {
            flashLightIv.setImageResource(R.drawable.ic_open);
            flashLightTv.setText(R.string.close_flashlight);
        } else {
            flashLightIv.setImageResource(R.drawable.ic_close);
            flashLightTv.setText(R.string.open_flashlight);
        }

    }

    /**
     * @param rawResult 返回的扫描结果
     */
    public void handleDecode(Result rawResult) {
        inactivityTimer.onActivity();
        vibrate();
        beepManager.playBeepSoundAndVibrate();

//        Intent intent = getIntent();
//        intent.putExtra(Constant.CODED_CONTENT, rawResult.getText());
//        setResult(RESULT_OK, intent);
//        this.finish();
        onScanQRCodeSuccess(rawResult.getText());
    }


    private void switchVisibility(View view, boolean b) {
        if (b) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }


    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            return;
        }
        try {
            // 打开Camera硬件设备
            cameraManager.openDriver(surfaceHolder);
            // 创建一个handler来打开预览，并抛出一个运行时异常
            if (handler == null) {
                handler = new CaptureActivityHandler(ScanActivity.this, cameraManager);
            }
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            Log.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    private void displayFrameworkBugMessageAndExit() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("扫一扫");
//        builder.setMessage(getString(R.string.msg_camera_framework_bug));
//        builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
//        builder.setOnCancelListener(new FinishListener(this));
//        builder.show();
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void showLoading() {
        showProgressDialog(getString(R.string.search_data));
    }

    @Override
    public void dismissLoading() {
        hideProgressDialog();
    }

    @Override
    public void showError(String msg) {

    }

    @Override
    public void setPresenter(ScanSearchChemicalContract.Presenter presenter) {
        scanSearchChemicalPresenter = (ScanSearchChemicalPresenter) presenter;
    }

    @Override
    public void showChemicalInfo(ChemicalInfo chemicalInfo, int inventory) {
        final Bundle bundle = new Bundle();
        if (inventory > 0) {
            bundle.putSerializable(Constant.INVENTORY, inventory);
        }
        if (!TextUtils.isEmpty(chemicalInfo.getId())) {
            bundle.putSerializable(Constant.INFO, chemicalInfo);
            skipActivity(ChemicalsDetailActivity.class, bundle);
            ScanActivity.this.finish();
        } else {
            Toast.makeText(ScanActivity.this, getString(R.string.not_search_data), Toast
                    .LENGTH_SHORT).show();
        }
    }
}
