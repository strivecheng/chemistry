package com.ruobilin.basf.basfchemical.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.ruobilin.basf.basfchemical.bean.FileInfo;

import java.util.List;

/**
 * Created by xingcc on 2018/12/7.
 * main function
 *
 * @author strivecheng
 */
@Dao
public interface FileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFileList(List<FileInfo> fileInfos);

    @Query("SELECT * FROM file")
    List<FileInfo> searchAllFile();

    @Query("SELECT * FROM file WHERE ChemicalId = :chemicalId")
    List<FileInfo> searchFilesByChemicalId(String chemicalId);

    @Query("SELECT * FROM file WHERE Path = :path")
    FileInfo searchFileByPath(String path);

    @Delete
     void deleteFiles(List<FileInfo> fileInfos);
}
