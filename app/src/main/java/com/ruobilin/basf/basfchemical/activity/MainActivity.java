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
import com.ruobilin.basf.basfchemical.contract.GetChemicalContract;
import com.ruobilin.basf.basfchemical.dao.AbstractMyChemicalDataBase;
import com.ruobilin.basf.basfchemical.dao.ChemicalDao;
import com.ruobilin.basf.basfchemical.dao.FileDao;
import com.ruobilin.basf.basfchemical.model.ChemicalModelImpl;
import com.ruobilin.basf.basfchemical.presenter.GetChemicalPresenter;
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
 *
 * @author xingcc
 */
public class MainActivity extends BaseActivity implements View.OnClickListener, EasyPermissions
        .PermissionCallbacks, GetChemicalContract.View {
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
    private GetChemicalPresenter getChemicalPresenter;

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
        getChemicalPresenter = new GetChemicalPresenter(ChemicalModelImpl.getInstance(this), this);
        getChemicalPresenter.getChemicalList();
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
                        getChemicalPresenter.goChemicalDetail(chemicalInfo);
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
                    getChemicalPresenter.getChemicalList();
                    break;
                default:
            }
        }
    }


    /**
     * 请求权限
     */
    private void requestCodeQRCodePermissions() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, getString(R.string
                    .request_camera_permission_tips), REQUEST_CODE_QRCODE_PERMISSIONS, perms);
        } else {
            skipScan();
        }
    }

    /**
     * 去扫一扫界面
     */
    private void skipScan() {
        Intent intent = new Intent(MainActivity.this, ScanActivity.class);
        startActivityForResult(intent, 10);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        skipScan();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Toast.makeText(this, getString(R.string.request_camera_permission_tips), Toast
                .LENGTH_SHORT).show();
    }


    @Override
    public void setPresenter(GetChemicalContract.Presenter presenter) {
        getChemicalPresenter = (GetChemicalPresenter) presenter;
    }

    @Override
    public void showLoading() {
        showProgressDialog(getString(R.string.getting_data));
    }

    @Override
    public void dismissLoading() {
        hideProgressDialog();
    }

    @Override
    public void showError(String msg) {

    }

    @Override
    public void showChemicalList(ArrayList<ChemicalInfo> chemicalInfos) {
        this.chemicalInfos.clear();
        this.chemicalInfos.addAll(chemicalInfos);
        chemicalListAdapter.notifyDataSetChanged();
        if (this.chemicalInfos.size() == 0) {
            mNoDataTv.setVisibility(View.VISIBLE);
        } else {
            mNoDataTv.setVisibility(View.GONE);
        }
    }

    @Override
    public void showChemicalDetail(ChemicalInfo chemicalInfo) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.INFO, chemicalInfo);
        skipActivity(ChemicalsDetailActivity.class, bundle);
    }
}
