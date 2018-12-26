package com.ruobilin.basf.basfchemical.contract;

import com.ruobilin.basf.basfchemical.base.BasePresenter;
import com.ruobilin.basf.basfchemical.base.BaseView;
import com.ruobilin.basf.basfchemical.bean.ChemicalInfo;

import java.util.ArrayList;

/**
 * Created by strive on 2018/12/26.
 * 二维码扫描查询化学药品的契约类
 * @author strive
 */

public interface ScanSearchChemicalContract{
    interface Presenter extends BasePresenter{
        void searchChemicalByCode(String code,int inventory);
    }

    interface View extends BaseView<Presenter>{
        void showChemicalInfo(ChemicalInfo chemicalInfo,int inventory);
    }
}
