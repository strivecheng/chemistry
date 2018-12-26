package com.ruobilin.basf.basfchemical.contract;

import com.ruobilin.basf.basfchemical.base.BasePresenter;
import com.ruobilin.basf.basfchemical.base.BaseShowView;
import com.ruobilin.basf.basfchemical.base.BaseView;
import com.ruobilin.basf.basfchemical.bean.ChemicalInfo;

import java.util.ArrayList;

/**
 * Created by xingcc on 2018/12/26.
 * main function
 *  获取化学药品的契约类
 * @author strivecheng
 */

public interface GetChemicalContract {
    interface Presenter extends BasePresenter {
        void getChemicalList();
        void goChemicalDetail(ChemicalInfo chemicalInfo);
    }

    interface View extends BaseView<Presenter> {
        void showChemicalList(ArrayList<ChemicalInfo> chemicalInfos);
        void showChemicalDetail(ChemicalInfo chemicalInfo);
    }
}
