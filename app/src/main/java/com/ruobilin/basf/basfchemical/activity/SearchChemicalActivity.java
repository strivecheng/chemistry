package com.ruobilin.basf.basfchemical.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Create by xingcc on 2018/12/7
 * main function: 搜索药品界面
 *
 * @author xingcc
 */
public class SearchChemicalActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = SearchChemicalActivity.class.getSimpleName();
    private TextView mCancelTv;
    private TextView mNoDataTv;
    private RecyclerView mChemicalRv;
    private ChemicalListAdapter chemicalListAdapter;
    private ArrayList<ChemicalInfo> chemicalInfos;
    private ChemicalDao chemicalDao;
    private FileDao fileDao;
    private EditText mSearchEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_search_chemical;
    }

    @Override
    protected void initView() {
        chemicalInfos = new ArrayList<>();
        chemicalDao = AbstractMyChemicalDataBase
                .getInstance(this)
                .getChemicalDao();
        fileDao = AbstractMyChemicalDataBase.getInstance(this).getFileDao();
        mCancelTv = findViewById(R.id.cancel_tv);

        mChemicalRv = findViewById(R.id.chemical_list_rv);
        mNoDataTv = findViewById(R.id.no_data_tv);
        mSearchEt = findViewById(R.id.search_et);


    }

    @Override
    protected void initData() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mChemicalRv.setLayoutManager(layoutManager);
        chemicalListAdapter = new ChemicalListAdapter(chemicalInfos);
        mChemicalRv.setAdapter(chemicalListAdapter);
    }

    @Override
    protected void initClick() {
        mCancelTv.setOnClickListener(this);
        mSearchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchChemicalByKeyWord(s.toString());
            }
        });
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
            case R.id.cancel_tv:
                mSearchEt.setText("");
                finish();
                break;
            default:
        }
    }

    private void searchChemicalByKeyWord(final String keyWord) {
        if (TextUtils.isEmpty(keyWord)) {
            return;
        }
        Observable.create(new ObservableOnSubscribe<List<ChemicalInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<List<ChemicalInfo>> emitter)
                    throws Exception {
                List<ChemicalInfo> chemicalInfos = chemicalDao.searchByKeyword("%"+keyWord+"%");
                if (chemicalInfos!=null&&chemicalInfos.size()>0){
                        for (ChemicalInfo c : chemicalInfos) {
                            List<FileInfo> fileInfos = fileDao.searchFilesByChemicalId(c.getId());
                            c.setFileInfos(fileInfos);
                        }
                }
                if (chemicalInfos == null) {
                    chemicalInfos = new ArrayList<>();
                }
                emitter.onNext(chemicalInfos);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ChemicalInfo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
//                        showProgressDialog(getString(R.string.search_data));
                    }

                    @Override
                    public void onNext(List<ChemicalInfo> chemicalInfos) {
//                        hideProgressDialog();
                        SearchChemicalActivity.this.chemicalInfos.clear();
                        SearchChemicalActivity.this.chemicalInfos.addAll(chemicalInfos);
                        if (SearchChemicalActivity.this.chemicalInfos.size() == 0) {
                            mNoDataTv.setVisibility(View.VISIBLE);
                        } else {
                            mNoDataTv.setVisibility(View.GONE);
                        }
                        chemicalListAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
//                        hideProgressDialog();
                    }

                    @Override
                    public void onComplete() {
//                        hideProgressDialog();

                    }
                });
    }
}
