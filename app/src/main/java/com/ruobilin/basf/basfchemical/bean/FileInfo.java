package com.ruobilin.basf.basfchemical.bean;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by xingcc on 2018/12/5.
 * main function
 *,foreignKeys = @ForeignKey(entity = ChemicalInfo.class,parentColumns = "Id",
 childColumns = "ChemicalId"),indices = @Index(value={"ChemicalId"},unique = true)
 * @author strivecheng
 */

@Entity(tableName = "file",indices = @Index("ChemicalId"))
public class FileInfo implements Serializable {
    @NonNull
    @PrimaryKey
    private String Id;
    private String Path;
    private String ChemicalId;
    private String Name;

    @NonNull
    public String getId() {
        return Id;
    }

    public void setId(@NonNull String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPath() {
        return Path;
    }

    public void setPath(String path) {
        Path = path;
    }

    public String getChemicalId() {
        return ChemicalId;
    }

    public void setChemicalId(String chemicalId) {
        ChemicalId = chemicalId;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "ChemicalId='" + ChemicalId + '\'' +
                ", Name='" + Name + '\'' +
                ", Path='" + Path + '\'' +
                '}';
    }
}
