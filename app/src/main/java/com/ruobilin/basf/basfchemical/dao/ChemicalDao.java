package com.ruobilin.basf.basfchemical.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.ruobilin.basf.basfchemical.bean.ChemicalInfo;

import java.util.List;

/**
 * Created by xingcc on 2018/12/7.
 * main function
 *
 * @author strivecheng
 */
@Dao
public interface ChemicalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertChemicalList(List<ChemicalInfo> chemicalInfos);

    @Query("SELECT * FROM chemical")
    List<ChemicalInfo> searchAllChemical();

    @Query("SELECT * FROM chemical WHERE Code LIKE :code LIMIT 1")
    ChemicalInfo searchByCodeOrId(String code);

    @Query("SELECT * FROM chemical WHERE Name LIKE :keyword OR Code LIKE :keyword OR CASNumber LIKE :keyword OR CMR LIKE :keyword OR HPhrase LIKE :keyword")
    List<ChemicalInfo> searchByKeyword(String keyword);

    @Delete
     void deleteChemicals(List<ChemicalInfo> chemicalInfos);

    //    @Query("SELECT * FROM chemical WHERE Name LIKE '%' || :keyword || '%' ")


}
