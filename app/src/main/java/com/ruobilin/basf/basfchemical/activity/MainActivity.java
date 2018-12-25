package com.ruobilin.basf.basfchemical.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ruobilin.basf.basfchemical.R;
import com.ruobilin.basf.basfchemical.adapter.ChemicalListAdapter;
import com.ruobilin.basf.basfchemical.base.BaseActivity;
import com.ruobilin.basf.basfchemical.bean.ChemicalInfo;
import com.ruobilin.basf.basfchemical.bean.FileInfo;
import com.ruobilin.basf.basfchemical.common.Constant;
import com.ruobilin.basf.basfchemical.dao.AbstractMyChemicalDataBase;
import com.ruobilin.basf.basfchemical.dao.ChemicalDao;
import com.ruobilin.basf.basfchemical.dao.FileDao;
import com.ruobilin.basf.basfchemical.utils.DateUtils;
import com.ruobilin.basf.basfchemical.utils.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * Create by xingcc on 2018/12/3
 * main function: 首页
 * 展示药品列表，可以搜索
 * 先从数据查找数据，没有数据，找本地存储数据去解析
 * @author xingcc
 */
public class MainActivity extends BaseActivity implements View.OnClickListener, EasyPermissions
        .PermissionCallbacks {
    private static final int REQUEST_CODE_QRCODE_PERMISSIONS = 1;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int GO_SETTING = 10;
    private ImageView mScanIv;
    private ImageView mSettingIv;
    private TextView mNoDataTv;
    private RecyclerView mChemicalRv;
    private LinearLayout mSearchLlt;
    private ChemicalListAdapter chemicalListAdapter;
    private ArrayList<ChemicalInfo> chemicalInfos;
    private ChemicalDao chemicalDao;
    private FileDao fileDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        chemicalInfos = new ArrayList<>();
        mScanIv = findViewById(R.id.scan_image);
        mSettingIv = findViewById(R.id.setting_image);
        mChemicalRv = findViewById(R.id.chemical_list_rv);
        mSearchLlt = findViewById(R.id.search_llt);
        mNoDataTv = findViewById(R.id.no_data_tv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mChemicalRv.setLayoutManager(layoutManager);
        chemicalListAdapter = new ChemicalListAdapter(chemicalInfos);
        mChemicalRv.setAdapter(chemicalListAdapter);
    }

    @Override
    protected void initData() {
        chemicalDao = AbstractMyChemicalDataBase
                .getInstance(this)
                .getChemicalDao();
        fileDao = AbstractMyChemicalDataBase.getInstance(this).getFileDao();
        getChemicalData();
    }

    /**
     * 获取数据
     */
    private void getChemicalData() {
        Observable.create(new ObservableOnSubscribe<List<ChemicalInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<List<ChemicalInfo>> emitter) throws Exception {
                //先从数据库查找
                List<ChemicalInfo> chemicalInfoList = chemicalDao.searchAllChemical();
                if (chemicalInfoList == null || chemicalInfoList.size() == 0) {
                    File newFile = new File(Environment.getExternalStorageDirectory().getPath() +
                            File.separator + Constant.DATA_PATH);
                    chemicalInfoList = findAndAnalysisXml(newFile);
                } else {
                    for (ChemicalInfo c : chemicalInfoList) {
                        List<FileInfo> fileInfos = fileDao.searchFilesByChemicalId(c.getId());
                        c.setFileInfos(fileInfos);
                    }
                }
                if (chemicalInfoList == null) {
                    chemicalInfoList = new ArrayList<>();
                }
                emitter.onNext(chemicalInfoList);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ChemicalInfo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        showProgressDialog(getString(R.string.getting_data));
                    }
                    @Override
                    public void onNext(List<ChemicalInfo> chemicalInfos) {
                        hideProgressDialog();
                        MainActivity.this.chemicalInfos.clear();
                        MainActivity.this.chemicalInfos.addAll(chemicalInfos);
                        chemicalListAdapter.notifyDataSetChanged();
                        if (MainActivity.this.chemicalInfos.size() == 0) {
                            mNoDataTv.setVisibility(View.VISIBLE);
                        } else {
                            mNoDataTv.setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                        hideProgressDialog();
                    }

                    @Override
                    public void onComplete() {
                        hideProgressDialog();
                    }
                });
    }

    @Override
    protected void initClick() {
        mScanIv.setOnClickListener(this);
        mSettingIv.setOnClickListener(this);
        mSearchLlt.setOnClickListener(this);
        chemicalListAdapter.setOnItemChildClickListener(new BaseQuickAdapter
                .OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                ChemicalInfo chemicalInfo = chemicalListAdapter.getItem(position);
                if (chemicalInfo == null) {
                    return;
                }
                switch (view.getId()) {
                    case R.id.m_chemical_list_card_view:
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Constant.INFO, chemicalInfo);
                        skipActivity(ChemicalsDetailActivity.class, bundle);
                        break;
                    default:
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scan_image:
                requestCodeQRCodePermissions();
                break;
            case R.id.setting_image:
                skipActivityForResult(SettingActivity.class, null, GO_SETTING);
                break;
            case R.id.search_llt:
                skipActivity(SearchChemicalActivity.class);
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GO_SETTING:
                    getChemicalData();
                    break;
                default:
            }
        }
    }


    private void requestCodeQRCodePermissions() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, getString(R.string
                    .request_camera_permission_tips), REQUEST_CODE_QRCODE_PERMISSIONS, perms);
        } else {
            skipScan();
        }
    }

    private void skipScan() {
        Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                                /*ZxingConfig是配置类
                                 *可以设置是否显示底部布局，闪光灯，相册，
                                 * 是否播放提示音  震动
                                 * 设置扫描框颜色等
                                 * 也可以不传这个参数
                                 * */
//                                ZxingConfig config = new ZxingConfig();
//                                config.setPlayBeep(false);//是否播放扫描声音 默认为true
//                                config.setShake(false);//是否震动  默认为true
//                                config.setDecodeBarCode(false);//是否扫描条形码 默认为true
//                                config.setReactColor(R.color.colorAccent);//设置扫描框四个角的颜色 默认为白色
//                                config.setFrameLineColor(R.color.colorAccent);//设置扫描框边框颜色 默认无色
//                                config.setScanLineColor(R.color.colorAccent);//设置扫描线的颜色 默认白色
//                                config.setFullScreenScan(false);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
//                                intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
        startActivityForResult(intent, 10);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
//        skipActivity(ScanActivity.class);
        skipScan();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Toast.makeText(this, getString(R.string.request_camera_permission_tips), Toast
                .LENGTH_SHORT).show();
    }

    /**
     * 找出xml文件并解析
     *
     * @param newFile
     */
    private ArrayList<ChemicalInfo> findAndAnalysisXml(File newFile) {
        File[] files = newFile.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return (pathname.isFile() && pathname.getName().endsWith(".xml"));
            }
        });
        if (files == null) {
            return null;
        }
        for (File f1 : files) {
            ArrayList<ChemicalInfo> chemicalInfos = FileUtils.analysisXml(f1);
            if (chemicalInfos.size() > 0) {
                //药品存入数据库
                chemicalDao.insertChemicalList(chemicalInfos);
                //文件存入数据库
                for (ChemicalInfo c : chemicalInfos) {
                    if (c.getFileInfos() != null) {
                        fileDao.insertFileList(c.getFileInfos());
                    }
                }
                String releaseDate = chemicalInfos.get(0).getReleaseDate();
                if (TextUtils.isEmpty(releaseDate)) {
                    releaseDate = DateUtils.getCurrentDateForYMD(new Date());
                }
                //发布版本存储到本地
                SharedPreferences sharedPreferences = getSharedPreferences(Constant
                        .MY_SHARE_PREFERENCES, MODE_PRIVATE);
                sharedPreferences.edit().putString(Constant.RELEASE_DATE, releaseDate).apply();
                return chemicalInfos;
            }
        }
        return null;
    }
}
