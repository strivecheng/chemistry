package com.ruobilin.basf.basfchemical.bean;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xingcc on 2018/12/5.
 * main function
 *
 * @author strivecheng
 */
@Entity(tableName = "chemical")
public class ChemicalInfo implements Serializable {
    @NonNull
    @PrimaryKey
    private String Id;
    private String Name;
    private String Code;
    private String CASNumber;
    private String IsDangerous;
    private String CMR;
    private String HPhrase;
    private String MSDSPublicDate;
    private String ReleaseDate;
    @Ignore
    private List<FileInfo> fileInfos;

    public List<FileInfo> getFileInfos() {
        return fileInfos;
    }

    public void setFileInfos(List<FileInfo> fileInfos) {
        this.fileInfos = fileInfos;
    }

    public ChemicalInfo() {
    }

//    public FileInfo getFileInfo() {
//        return fileInfo;
//    }
//
//    public void setFileInfo(FileInfo fileInfo) {
//        this.fileInfo = fileInfo;
//    }

    public String getReleaseDate() {
        if (TextUtils.isEmpty(ReleaseDate)) {
            return "";
        }
        return ReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        ReleaseDate = releaseDate;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getCASNumber() {
        return CASNumber;
    }

    public void setCASNumber(String CASNumber) {
        this.CASNumber = CASNumber;
    }

    public String getIsDangerous() {
        if (TextUtils.isEmpty(IsDangerous)) {
            return "";
        }
        return IsDangerous;
    }

    public void setIsDangerous(String dangerous) {
        IsDangerous = dangerous;
    }

    public String getCMR() {
        if (TextUtils.isEmpty(CMR)) {
            return "";
        }
        return CMR;
    }

    public void setCMR(String CMR) {
        this.CMR = CMR;
    }

    public String getHPhrase() {
        if (TextUtils.isEmpty(HPhrase)) {
            return "";
        }
        return HPhrase;
    }

    public void setHPhrase(String HPhrase) {
        this.HPhrase = HPhrase;
    }

    public String getMSDSPublicDate() {
        if (TextUtils.isEmpty(MSDSPublicDate)) {
            return "";
        }
        return MSDSPublicDate;
    }

    public void setMSDSPublicDate(String MSDSPublicDate) {
        this.MSDSPublicDate = MSDSPublicDate;
    }
}
