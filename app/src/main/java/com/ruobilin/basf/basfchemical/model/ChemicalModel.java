package com.ruobilin.basf.basfchemical.model;

import com.ruobilin.basf.basfchemical.listener.ChemicalListener;

/**
 * Created by xingcc on 2018/12/10.
 * main function
 *
 * @author strivecheng
 */

public interface ChemicalModel {
    void getChemicalList(ChemicalListener listener);

    void getChemicalInfoByQRCode(String code,ChemicalListener listener);

    void searchChemicalList(String keyWord,ChemicalListener listener);

    void getFileList(String chemicalId);

    void getFileInfo();
}
