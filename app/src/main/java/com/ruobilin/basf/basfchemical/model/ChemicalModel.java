package com.ruobilin.basf.basfchemical.model;

import com.ruobilin.basf.basfchemical.base.BaseRequestCallback;
import com.ruobilin.basf.basfchemical.bean.ChemicalInfo;
import com.ruobilin.basf.basfchemical.listener.ChemicalListener;

import java.util.ArrayList;

/**
 * Created by xingcc on 2018/12/10.
 * main function
 *
 * @author strivecheng
 */

public interface ChemicalModel {
    interface GetChemicalListCallback extends BaseRequestCallback {
        void getChemicalListSuccess(ArrayList<ChemicalInfo> chemicalInfos);
    }

    interface GetChemicalInfoCallback extends BaseRequestCallback {
        void getChemicalInfoByQRCodeSuccess(ChemicalInfo chemicalInfo,int inventory);
    }

    void getChemicalList(GetChemicalListCallback callback);

    void searchChemicalInfoByCode(String code,int inventory,GetChemicalInfoCallback chemicalInfoCallback);

    void searchChemicalList(String keyWord,GetChemicalListCallback callback);

    void getFileList(String chemicalId);

    void getFileInfo();
}
