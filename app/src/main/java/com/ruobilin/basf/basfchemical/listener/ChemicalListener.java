package com.ruobilin.basf.basfchemical.listener;

import com.ruobilin.basf.basfchemical.base.BaseRequestCallback;
import com.ruobilin.basf.basfchemical.bean.ChemicalInfo;
import com.ruobilin.basf.basfchemical.bean.FileInfo;

import java.util.ArrayList;

/**
 * Created by xingcc on 2018/12/10.
 * main function
 *
 * @author strivecheng
 */

public interface ChemicalListener extends BaseRequestCallback {

    void getChemicalListSuccess(ArrayList<ChemicalInfo> chemicalInfos);

    void getChemicalInfoByQRCodeSuccess(ChemicalInfo chemicalInfo);

    void searchChemicalListSuccess(ArrayList<ChemicalInfo> chemicalInfos);

    void getFileListSuccess(ArrayList<FileInfo> fileInfos);

    void getFileInfoSuccess(FileInfo fileInfo);
}
