package com.ruobilin.basf.basfchemical.dao;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.ruobilin.basf.basfchemical.bean.ChemicalInfo;
import com.ruobilin.basf.basfchemical.bean.FileInfo;

/**
 * Created by xingcc on 2018/12/7.
 * main function
 * 数据库管理类
 * @author strivecheng
 */

@Database(entities = {ChemicalInfo.class, FileInfo.class}, version = 1, exportSchema = false)
public abstract class AbstractMyChemicalDataBase extends RoomDatabase {
    private static final String DB_NAME = "chemical_database";
    private static volatile AbstractMyChemicalDataBase INSTANCE;

    public static AbstractMyChemicalDataBase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AbstractMyChemicalDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = createChemicalDataBase(context);
                }
            }
        }
        return INSTANCE;
    }

    private static AbstractMyChemicalDataBase createChemicalDataBase(Context context) {
        return Room.databaseBuilder(context.getApplicationContext(),
                AbstractMyChemicalDataBase.class, DB_NAME)
                .fallbackToDestructiveMigration()
                .build();
    }

    public abstract ChemicalDao getChemicalDao();

    public abstract FileDao getFileDao();


}
