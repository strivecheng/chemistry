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
import com.ruobilin.basf.basfchemical.contract.SearchChemicalContract;
import com.ruobilin.basf.basfchemical.dao.AbstractMyChemicalDataBase;
import com.ruobilin.basf.basfchemical.dao.ChemicalDao;
import com.ruobilin.basf.basfchemical.dao.FileDao;
import com.ruobilin.basf.basfchemical.model.ChemicalModelImpl;
import com.ruobilin.basf.basfchemical.presenter.SearchChemicalPresenter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Create by xingcc on 2018/12/7
 * main function: 搜索药品界面
 *
 * @author xingcc
 */
public class SearchChemicalActivity extends BaseActivity implements View.OnClickListener, SearchChemicalContract.View {

    private static final String TAG = SearchChemicalActivity.class.getSimpleName();
    private TextView mCancelTv;
    private TextView mNoDataTv;
    private RecyclerView mChemicalRv;
    private ChemicalListAdapter chemicalListAdapter;
    private ArrayList<ChemicalInfo> chemicalInfos;
    private EditText mSearchEt;
    private SearchChemicalPresenter searchChemicalPresenter;

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

        mCancelTv = findViewById(R.id.cancel_tv);

        mChemicalRv = findViewById(R.id.chemical_list_rv);
        mNoDataTv = findViewById(R.id.no_data_tv);
        mSearchEt = findViewById(R.id.search_et);


    }

    @Override
    protected void initData() {
        searchChemicalPresenter = new SearchChemicalPresenter(ChemicalModelImpl.getInstance(this), this);

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
                searchChemicalPresenter.searchChemicalByKeyword(s.toString());
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

    @Override
    public void showLoading() {
//        showProgressDialog(getString(R.string.search_data));
    }

    @Override
    public void dismissLoading() {
//        hideProgressDialog();
    }

    @Override
    public void showError(String msg) {

    }

    @Override
    public void setPresenter(SearchChemicalContract.Presenter presenter) {
        searchChemicalPresenter = (SearchChemicalPresenter) checkNotNull(presenter);
    }

    @Override
    public void showChemicalList(ArrayList<ChemicalInfo> chemicalInfos) {
        this.chemicalInfos.clear();
        this.chemicalInfos.addAll(chemicalInfos);
        if (this.chemicalInfos.size() == 0) {
            mNoDataTv.setVisibility(View.VISIBLE);
        } else {
            mNoDataTv.setVisibility(View.GONE);
        }
        chemicalListAdapter.notifyDataSetChanged();
    }
}
