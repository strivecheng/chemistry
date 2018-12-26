package com.ruobilin.basf.basfchemical.presenter;

import com.ruobilin.basf.basfchemical.base.BasePresenterImpl;
import com.ruobilin.basf.basfchemical.bean.ChemicalInfo;
import com.ruobilin.basf.basfchemical.contract.GetChemicalContract;
import com.ruobilin.basf.basfchemical.model.ChemicalModel;

import java.util.ArrayList;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Created by xingcc on 2018/12/26.
 * main function
 *
 * @author strivecheng
 */

public class GetChemicalPresenter extends BasePresenterImpl implements GetChemicalContract.Presenter, ChemicalModel.GetChemicalListCallback {
    private GetChemicalContract.View mChemicalListView;
    private ChemicalModel mChemicalModel;

    public GetChemicalPresenter(ChemicalModel chemicalModel,GetChemicalContract.View chemicalListView) {
        super(chemicalListView);
        this.mChemicalListView = checkNotNull(chemicalListView,"chemicalListView cannot be null");
        this.mChemicalModel = checkNotNull(chemicalModel,"chemicalModel cannot be null");
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unSubscribe() {

    }

    @Override
    public void getChemicalList() {
        mChemicalModel.getChemicalList(this);
    }

    @Override
    public void getChemicalListSuccess(ArrayList<ChemicalInfo> chemicalInfos) {
        mChemicalListView.showChemicalList(chemicalInfos);
    }
}
