package com.ruobilin.basf.basfchemical.model;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ruobilin.basf.basfchemical.R;
import com.ruobilin.basf.basfchemical.activity.ChemicalsDetailActivity;
import com.ruobilin.basf.basfchemical.activity.ScanActivity;
import com.ruobilin.basf.basfchemical.activity.SearchChemicalActivity;
import com.ruobilin.basf.basfchemical.bean.ChemicalInfo;
import com.ruobilin.basf.basfchemical.bean.FileInfo;
import com.ruobilin.basf.basfchemical.common.Constant;
import com.ruobilin.basf.basfchemical.dao.AbstractMyChemicalDataBase;
import com.ruobilin.basf.basfchemical.dao.ChemicalDao;
import com.ruobilin.basf.basfchemical.dao.FileDao;
import com.ruobilin.basf.basfchemical.listener.ChemicalListener;
import com.ruobilin.basf.basfchemical.utils.DateUtils;
import com.ruobilin.basf.basfchemical.utils.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by xingcc on 2018/12/26.
 * main function
 *
 * @author strivecheng
 */

public class ChemicalModelImpl implements ChemicalModel {
    /**
     * 数据库存储位置
     */
    private static final String SQL_PATH = Environment.getExternalStorageDirectory().getPath() +
            File.separator + Constant.DATA_PATH;
    @Nullable
    private static ChemicalModelImpl INSTANCE;
    private ChemicalDao chemicalDao;
    private FileDao fileDao;

    public ChemicalModelImpl(Context context) {
        checkNotNull(context, "context cannot be null");
        chemicalDao = AbstractMyChemicalDataBase
                .getInstance(context)
                .getChemicalDao();
        fileDao = AbstractMyChemicalDataBase.getInstance(context).getFileDao();
    }

    /**
     * 单例
     * @param context
     * @return
     */
    public static ChemicalModelImpl getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ChemicalModelImpl(context);
        }
        return INSTANCE;
    }

    @Override
    public void getChemicalList(GetChemicalListCallback callback) {
        getChemicalData(callback);
    }

    @Override
    public void searchChemicalInfoByCode(final String code, final int inventory, final GetChemicalInfoCallback chemicalInfoCallback) {
        Observable.create(new ObservableOnSubscribe<ChemicalInfo>() {
            @Override
            public void subscribe(ObservableEmitter<ChemicalInfo> emitter) throws Exception {
                ChemicalInfo chemicalInfo = chemicalDao.searchByCodeOrId(code);
                if (chemicalInfo != null) {
                    List<FileInfo> fileInfos = fileDao.searchFilesByChemicalId(chemicalInfo.getId());
                    chemicalInfo.setFileInfos(fileInfos);
                } else {
                    chemicalInfo = new ChemicalInfo();
                }
                emitter.onNext(chemicalInfo);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ChemicalInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        chemicalInfoCallback.onStart();

                    }

                    @Override
                    public void onNext(ChemicalInfo chemicalInfo) {
                        chemicalInfoCallback.getChemicalInfoByQRCodeSuccess(chemicalInfo,inventory);

                    }

                    @Override
                    public void onError(Throwable e) {
                        chemicalInfoCallback.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        chemicalInfoCallback.onComplete();
                    }
                });
    }

    @Override
    public void searchChemicalList(String keyWord, GetChemicalListCallback callback) {
        searchChemicalByKeyWord(keyWord, callback);
    }

    @Override
    public void getFileList(String chemicalId) {

    }

    @Override
    public void getFileInfo() {

    }

    /**
     * 获取数据
     */
    private void getChemicalData(final GetChemicalListCallback callback) {
        Observable.create(new ObservableOnSubscribe<List<ChemicalInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<List<ChemicalInfo>> emitter) throws Exception {
                //先从数据库查找
                List<ChemicalInfo> chemicalInfoList = chemicalDao.searchAllChemical();
                if (chemicalInfoList == null || chemicalInfoList.size() == 0) {
                    File newFile = new File(SQL_PATH);
                    chemicalInfoList = findAndAnalysisXml(newFile);
                } else {
                    for (ChemicalInfo c : chemicalInfoList) {
                        List<FileInfo> fileInfos = fileDao.searchFilesByChemicalId(c.getId());
                        c.setFileInfos(fileInfos);
                    }
                }
                if (chemicalInfoList == null) {
                    chemicalInfoList = new ArrayList<>();
                }
                emitter.onNext(chemicalInfoList);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ChemicalInfo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        callback.onStart();
//                        showProgressDialog(getString(R.string.getting_data));
                    }

                    @Override
                    public void onNext(List<ChemicalInfo> chemicalInfos) {
                        callback.getChemicalListSuccess((ArrayList<ChemicalInfo>) chemicalInfos);
//
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        callback.onComplete();
                    }
                });
    }

    /**
     * 找出xml文件并解析
     *
     * @param newFile
     */
    private ArrayList<ChemicalInfo> findAndAnalysisXml(File newFile) {
        File[] files = newFile.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return (pathname.isFile() && pathname.getName().endsWith(".xml"));
            }
        });
        if (files == null) {
            return null;
        }
        for (File f1 : files) {
            ArrayList<ChemicalInfo> chemicalInfos = FileUtils.analysisXml(f1);
            if (chemicalInfos.size() > 0) {
                //药品存入数据库
                chemicalDao.insertChemicalList(chemicalInfos);
                //文件存入数据库
                for (ChemicalInfo c : chemicalInfos) {
                    if (c.getFileInfos() != null) {
                        fileDao.insertFileList(c.getFileInfos());
                    }
                }
                String releaseDate = chemicalInfos.get(0).getReleaseDate();
                if (TextUtils.isEmpty(releaseDate)) {
                    releaseDate = DateUtils.getCurrentDateForYMD(new Date());
                }
                //发布版本存储到本地
//                SharedPreferences sharedPreferences = getSharedPreferences(Constant
//                        .MY_SHARE_PREFERENCES, MODE_PRIVATE);
//                sharedPreferences.edit().putString(Constant.RELEASE_DATE, releaseDate).apply();
                return chemicalInfos;
            }
        }
        return null;
    }

    /**
     * 根据关键字查找数据
     *
     * @param keyWord
     */
    private void searchChemicalByKeyWord(final String keyWord, final GetChemicalListCallback
            callback) {
        if (TextUtils.isEmpty(keyWord)) {
            return;
        }
        Observable.create(new ObservableOnSubscribe<List<ChemicalInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<List<ChemicalInfo>> emitter)
                    throws Exception {
                List<ChemicalInfo> chemicalInfos = chemicalDao.searchByKeyword("%" + keyWord + "%");
                if (chemicalInfos != null && chemicalInfos.size() > 0) {
                    for (ChemicalInfo c : chemicalInfos) {
                        List<FileInfo> fileInfos = fileDao.searchFilesByChemicalId(c.getId());
                        c.setFileInfos(fileInfos);
                    }
                }
                if (chemicalInfos == null) {
                    chemicalInfos = new ArrayList<>();
                }
                emitter.onNext(chemicalInfos);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ChemicalInfo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        callback.onStart();
                    }

                    @Override
                    public void onNext(List<ChemicalInfo> chemicalInfos) {
                        callback.getChemicalListSuccess((ArrayList<ChemicalInfo>) chemicalInfos);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        callback.onComplete();
                    }
                });
    }

}
