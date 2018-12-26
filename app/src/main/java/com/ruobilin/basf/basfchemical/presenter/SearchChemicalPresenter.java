package com.ruobilin.basf.basfchemical.presenter;

import com.ruobilin.basf.basfchemical.base.BasePresenterImpl;
import com.ruobilin.basf.basfchemical.base.BaseShowView;
import com.ruobilin.basf.basfchemical.bean.ChemicalInfo;
import com.ruobilin.basf.basfchemical.contract.SearchChemicalContract;
import com.ruobilin.basf.basfchemical.model.ChemicalModel;

import java.util.ArrayList;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by strive on 2018/12/26.
 */

public class SearchChemicalPresenter extends BasePresenterImpl implements SearchChemicalContract.Presenter,ChemicalModel.GetChemicalListCallback {
    private ChemicalModel mChemicalModel;
    private SearchChemicalContract.View mChemicalListView;

    public SearchChemicalPresenter(ChemicalModel chemicalModel,SearchChemicalContract.View chemicalListView) {
        super(chemicalListView);
        checkNotNull(chemicalModel,"chemicalModel cannot be null");
        checkNotNull(chemicalListView,"chemicalListView cannot be null");
        this.mChemicalListView = chemicalListView;
        this.mChemicalModel = chemicalModel;
    }


    @Override
    public void subscribe() {

    }

    @Override
    public void unSubscribe() {

    }

    @Override
    public void getChemicalListSuccess(ArrayList<ChemicalInfo> chemicalInfos) {
        mChemicalListView.showChemicalList(chemicalInfos);
    }

    @Override
    public void searchChemicalByKeyword(String keyword) {
        mChemicalModel.searchChemicalList(keyword,this);
    }
}
