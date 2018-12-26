package com.ruobilin.basf.basfchemical.contract;

import com.ruobilin.basf.basfchemical.base.BasePresenter;
import com.ruobilin.basf.basfchemical.base.BaseView;
import com.ruobilin.basf.basfchemical.bean.ChemicalInfo;

import java.util.ArrayList;

/**
 * Created by strive on 2018/12/26.
 * 查询化学药品的契约类
 * @author strive
 */

public interface SearchChemicalContract {
    interface Presenter extends BasePresenter{
        void searchChemicalByKeyword(String keyword);
    }

    interface View extends BaseView<Presenter>{
        void showChemicalList(ArrayList<ChemicalInfo> chemicalInfos);
    }
}
