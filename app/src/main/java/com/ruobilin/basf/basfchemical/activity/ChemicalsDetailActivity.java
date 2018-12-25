package com.ruobilin.basf.basfchemical.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ruobilin.basf.basfchemical.R;
import com.ruobilin.basf.basfchemical.adapter.FileListAdapter;
import com.ruobilin.basf.basfchemical.base.BaseActivity;
import com.ruobilin.basf.basfchemical.bean.ChemicalInfo;
import com.ruobilin.basf.basfchemical.bean.FileInfo;
import com.ruobilin.basf.basfchemical.common.Constant;

import java.io.File;
import java.util.ArrayList;

/**
 * Create by xingcc on 2018/12/3
 * main function: 药品详情界面
 *
 * @author xingcc
 */
public class ChemicalsDetailActivity extends BaseActivity implements View.OnClickListener {

    private ChemicalInfo chemicalInfo;
    private ImageView mBackImage;
    private TextView mChemicalNameTv;
    private TextView mChemicalCodeTv;
    private TextView mChemicalCASCodeTv;
    private TextView mChemicalDangerousTv;
    private TextView mChemicalReleaseDateTv;
    private TextView mChemicalCMRTv;
    private TextView mChemicalHPhraseTv;
    private RecyclerView mFilesRv;
    private FileListAdapter fileListAdapter;
    private ArrayList<FileInfo> fileInfos;
    private TextView mChemicalInventoryTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_chemicals_detail;
    }

    @Override
    protected void initView() {
        fileInfos = new ArrayList<>();

        mBackImage = findViewById(R.id.back_image);
        mChemicalNameTv = findViewById(R.id.chemical_name_tv);
        mChemicalCodeTv = findViewById(R.id.chemical_code_tv);
        mChemicalCASCodeTv = findViewById(R.id.chemical_cas_code_tv);
        mChemicalDangerousTv = findViewById(R.id.chemical_dangerous_tv);
        mChemicalReleaseDateTv = findViewById(R.id.chemical_release_date_tv);
        mChemicalCMRTv = findViewById(R.id.chemical_cmr_tv);
        mChemicalHPhraseTv = findViewById(R.id.chemical_h_phrase_tv);
        mChemicalInventoryTv = findViewById(R.id.chemical_inventory_tv);
        mFilesRv = findViewById(R.id.files_rv);
        mFilesRv.setLayoutManager(new LinearLayoutManager(this));
        mFilesRv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        fileListAdapter = new FileListAdapter(fileInfos);
        mFilesRv.setAdapter(fileListAdapter);

    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            chemicalInfo = ((ChemicalInfo) intentExtras.getSerializable(Constant.INFO));
            if (intentExtras.containsKey(Constant.INVENTORY)) {
                mChemicalInventoryTv.setText(String.valueOf(intentExtras.getInt(Constant.INVENTORY)));
            }
        }

        if (chemicalInfo != null) {
            mChemicalNameTv.setText(chemicalInfo.getName());
            mChemicalCodeTv.setText(chemicalInfo.getCode());
            mChemicalCASCodeTv.setText(chemicalInfo.getCASNumber());
            mChemicalDangerousTv.setText(chemicalInfo.getIsDangerous());
            mChemicalReleaseDateTv.setText(chemicalInfo.getMSDSPublicDate());
            mChemicalCMRTv.setText(chemicalInfo.getCMR());
            mChemicalHPhraseTv.setText(chemicalInfo.getHPhrase());
            if (chemicalInfo.getFileInfos() != null && chemicalInfo.getFileInfos().size() > 0) {
                this.fileInfos.clear();
                this.fileInfos.addAll(chemicalInfo.getFileInfos());
                fileListAdapter.notifyDataSetChanged();
            }
        }

    }

    @Override
    protected void initClick() {
        mBackImage.setOnClickListener(this);
        fileListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FileInfo fileInfo = fileListAdapter.getItem(position);
                if (fileInfo == null) {
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString(Constant.PATH, Environment.getExternalStorageDirectory().getPath() + File.separator + Constant.DATA_PATH + File.separator + fileInfo.getPath());
                skipActivity(PDFPreviewActivity.class, bundle);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_image:
                finish();
                break;
            default:
        }
    }
}
