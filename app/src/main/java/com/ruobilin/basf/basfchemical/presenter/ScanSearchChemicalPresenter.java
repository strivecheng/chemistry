package com.ruobilin.basf.basfchemical.presenter;

import com.ruobilin.basf.basfchemical.base.BasePresenterImpl;
import com.ruobilin.basf.basfchemical.base.BaseShowView;
import com.ruobilin.basf.basfchemical.bean.ChemicalInfo;
import com.ruobilin.basf.basfchemical.contract.ScanSearchChemicalContract;
import com.ruobilin.basf.basfchemical.contract.SearchChemicalContract;
import com.ruobilin.basf.basfchemical.model.ChemicalModel;

import java.util.ArrayList;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by strive on 2018/12/26.
 */

public class ScanSearchChemicalPresenter extends BasePresenterImpl implements
        ScanSearchChemicalContract.Presenter, ChemicalModel.GetChemicalInfoCallback {
    private ChemicalModel mChemicalModel;
    private ScanSearchChemicalContract.View mShowChemicalView;

    public ScanSearchChemicalPresenter(ChemicalModel chemicalModel, ScanSearchChemicalContract
            .View showChemicalView) {
        super(showChemicalView);
        checkNotNull(chemicalModel, "chemicalModel cannot be null");
        checkNotNull(showChemicalView, "showChemicalView cannot be null");
        this.mShowChemicalView = showChemicalView;
        this.mChemicalModel = chemicalModel;
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unSubscribe() {

    }

    @Override
    public void searchChemicalByCode(String code,int inventory) {
        mChemicalModel.searchChemicalInfoByCode(code,inventory,this);
    }

    @Override
    public void getChemicalInfoByQRCodeSuccess(ChemicalInfo chemicalInfo,int inventory) {
        mShowChemicalView.showChemicalInfo(chemicalInfo,inventory);
    }
}
