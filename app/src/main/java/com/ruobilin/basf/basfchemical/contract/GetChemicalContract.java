package com.ruobilin.basf.basfchemical.contract;

import com.ruobilin.basf.basfchemical.base.BasePresenter;
import com.ruobilin.basf.basfchemical.base.BaseShowView;
import com.ruobilin.basf.basfchemical.bean.ChemicalInfo;

import java.util.ArrayList;

/**
 * Created by xingcc on 2018/12/26.
 * main function
 *
 * @author strivecheng
 */

public interface GetChemicalContract {
    interface Presenter extends BasePresenter {
        void getChemicalList();
    }

    interface View extends BaseShowView {
        void showChemicalList(ArrayList<ChemicalInfo> chemicalInfos);
    }
}
