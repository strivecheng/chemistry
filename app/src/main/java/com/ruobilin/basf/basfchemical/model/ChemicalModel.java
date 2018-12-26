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
        void getChemicalInfoByQRCodeSuccess(ChemicalInfo chemicalInfo);
    }

    void getChemicalList(GetChemicalListCallback callback);

    void getChemicalInfoByQRCode(String code,GetChemicalInfoCallback chemicalInfoCallback);

    void searchChemicalList(String keyWord,ChemicalListener listener);

    void getFileList(String chemicalId);

    void getFileInfo();
}
