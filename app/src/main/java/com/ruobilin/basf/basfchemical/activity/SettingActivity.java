package com.ruobilin.basf.basfchemical.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ruobilin.basf.basfchemical.R;
import com.ruobilin.basf.basfchemical.base.BaseActivity;
import com.ruobilin.basf.basfchemical.bean.ChemicalInfo;
import com.ruobilin.basf.basfchemical.bean.FileInfo;
import com.ruobilin.basf.basfchemical.common.Constant;
import com.ruobilin.basf.basfchemical.dao.AbstractMyChemicalDataBase;
import com.ruobilin.basf.basfchemical.dao.ChemicalDao;
import com.ruobilin.basf.basfchemical.dao.FileDao;
import com.ruobilin.basf.basfchemical.utils.DateUtils;
import com.ruobilin.basf.basfchemical.utils.FileUtils;
import com.ruobilin.basf.basfchemical.utils.LanguageUtils;
import com.ruobilin.basf.basfchemical.utils.ZipUtils;

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
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Create by xingcc on 2018/12/3
 * main function: 设置界面
 * 语言切换，数据版本展示，可导入本地数据包
 *
 * @author xingcc
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener,
        EasyPermissions.PermissionCallbacks {

    private static final int SELECT_FILE = 1;
    private static final String TAG = SettingActivity.class.getSimpleName();
    private static final int REQUEST_WRITE_EXTERNAL_PERMISSIONS = 10;
    private RelativeLayout mLanguageRl;
    private RelativeLayout mVersionRl;
    private TextView mLanguageTv;
    private TextView mVersionTv;
    private ImageView mBackImage;
    private Button mImportBtn;
    private String selectLanguage = "";
    private int selectLanguageItem = 0;
    SharedPreferences sharedPreferences = null;
    private ChemicalDao chemicalDao;
    private FileDao fileDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {
        chemicalDao = AbstractMyChemicalDataBase
                .getInstance(this)
                .getChemicalDao();
        fileDao = AbstractMyChemicalDataBase.getInstance(this).getFileDao();

        sharedPreferences = getSharedPreferences(Constant.MY_SHARE_PREFERENCES, MODE_PRIVATE);


        mLanguageRl = findViewById(R.id.language_rl);
        mLanguageTv = findViewById(R.id.language_tv);

        mVersionRl = findViewById(R.id.version_rl);
        mVersionTv = findViewById(R.id.version_tv);

        mImportBtn = findViewById(R.id.import_btn);
        mBackImage = findViewById(R.id.back_image);

    }

    @Override
    protected void initData() {
        String language = sharedPreferences.getString(Constant.LANGUAGE, "");
        if (Constant.SIMPLIFIED_CHINESE.equals(language)) {
            selectLanguageItem = 0;
            mLanguageTv.setText(getString(R.string.simple_chinese));
        } else if (Constant.ENGLISH.equals(language)) {
            selectLanguageItem = 1;
            mLanguageTv.setText(getString(R.string.english));

        }
        setReleaseDate();
    }

    private void setReleaseDate() {
        if (sharedPreferences.contains(Constant.RELEASE_DATE)) {
            String string = sharedPreferences.getString(Constant.RELEASE_DATE, "");
            mVersionTv.setText(string);
        }
    }

    @Override
    protected void initClick() {
        mLanguageRl.setOnClickListener(this);
        mBackImage.setOnClickListener(this);
        mImportBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.language_rl:
                showSelectLanguageDialog();
                break;
            case R.id.import_btn:
                requestWritePermissions();
                break;
            case R.id.back_image:
                finish();
                break;
            default:
        }
    }

    /**
     * 语言选择
     */
    private void showSelectLanguageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] strings = {getString(R.string.simple_chinese), getString(R.string.english)};
        builder.setTitle(R.string.setting_language)
                .setSingleChoiceItems(strings, selectLanguageItem, new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (selectLanguageItem == which) {
                            dialog.dismiss();
                            return;
                        }
                        switch (which) {
                            case 0:
                                selectLanguage = Constant.SIMPLIFIED_CHINESE;
                                break;
                            case 1:
                                selectLanguage = Constant.ENGLISH;
                                break;
                            default:
                        }
                        dialog.dismiss();
                        LanguageUtils.changeAppLanguage(SettingActivity.this, selectLanguage);
                        saveLanguageToLocal(selectLanguage);
                        finish();
                        updateAppUI();
                    }
                })
                .setPositiveButton(R.string.cancel, null)
                .setCancelable(true).create().show();
    }


    private void saveLanguageToLocal(String selectLanguage) {
        sharedPreferences.edit().putString(Constant.LANGUAGE, selectLanguage).apply();
    }

    /**
     * 更新app界面语言
     */
    private void updateAppUI() {
        Intent it = new Intent(SettingActivity.this, MainActivity.class);
        //清空任务栈确保当前打开activity为前台任务栈栈顶
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(it);
    }


    /**
     * 去系统文件夹选择文件
     */
    private void getSystemFolder() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/zip");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, SELECT_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SELECT_FILE:
                    copySelectFile(data);
                    break;
                default:
            }
        }
    }

    /**
     * copy文件到指定目录
     *
     * @param data
     */
    private void copySelectFile(Intent data) {
        final Uri uri = data.getData();
        String path = "";
        if (uri != null) {
            path = FileUtils.getPath(this, uri);
            if (path != null) {
                //选择的zip文件
                File file = new File(path);
                //判读是否是zip文件
                if (!isZipFile(file)) {
                    Toast.makeText(this, R.string.please_selected_zip_file, Toast.LENGTH_SHORT).show();
                    return;
                }
                //需要复制到新的路径
                File newFile = new File(Environment.getExternalStorageDirectory().getPath() +
                        File.separator + Constant.DATA_PATH);
                if (!newFile.exists()) {
                    newFile.mkdirs();
                }
                //先删除该文件夹下的所有子文件
//                if (newFile.isDirectory()) {
//                    FileUtils.deleteFile(newFile);
//                }
                //开始复制
                startCopyFile(path, file, newFile);

            }
        }
    }

    /**
     * 判断是否是zip文件
     */
    private boolean isZipFile(File file) {
        if (file == null) {
            return false;
        }
        if (file.getName().endsWith(".zip")) {
            return true;
        }
        return false;
    }

    /**
     * 开始复制
     *
     * @param path
     * @param file
     * @param newFile
     */
    private void startCopyFile(final String path, final File file, final File newFile) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                //先删除该文件夹下的所有子文件
                if (newFile.isDirectory()) {
                    FileUtils.deleteFile(newFile,true);
                }
                emitter.onNext(FileUtils.copyFile(path, newFile.getPath() + File.separator + file.getName()));

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        showProgressDialog(getString(R.string.copying));

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {

                        if (aBoolean) {
                            Toast.makeText(SettingActivity.this, R.string.import_file_success, Toast
                                    .LENGTH_SHORT).show();
                            hideProgressDialog();

                            startUnZip(file, newFile);
                        }else {
                            hideProgressDialog();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(SettingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        hideProgressDialog();

                    }

                    @Override
                    public void onComplete() {
                        hideProgressDialog();
                    }
                });


    }

    /**
     * 开始解压
     *
     * @param file
     * @param newFile
     */
    private void startUnZip(final File file, final File newFile) {
        final File localFile = new File(newFile.getPath() + File.separator + file.getName());
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                //解压
                emitter.onNext(ZipUtils.unZipFiles(localFile, localFile.getParent()));

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        showProgressDialog(getString(R.string.unziping));
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {

                        if (aBoolean) {
                            hideProgressDialog();
                            if (localFile.exists()&&localFile.isFile()) {
                                localFile.delete();
                            }
                            Toast.makeText(SettingActivity.this, R.string.unzip_success, Toast
                                    .LENGTH_SHORT).show();
                            analysisXml();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(SettingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        hideProgressDialog();
                    }

                    @Override
                    public void onComplete() {
                        hideProgressDialog();
                    }
                });
    }

    /**
     * 开始解析数据
     */
    private void analysisXml() {
        Observable.create(new ObservableOnSubscribe<List<ChemicalInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<List<ChemicalInfo>> emitter)
                    throws Exception {
                File newFile = new File(Environment.getExternalStorageDirectory()
                        .getPath() + File.separator + Constant.DATA_PATH);
                ArrayList<ChemicalInfo> chemicalInfos = findAndAnalysisXml(newFile);
                emitter.onNext(chemicalInfos);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ChemicalInfo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        showProgressDialog(getString(R.string.analysis_data));
                    }

                    @Override
                    public void onNext(List<ChemicalInfo> chemicalInfos) {
                        hideProgressDialog();
                        setReleaseDate();
                        setResult(RESULT_OK);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                        hideProgressDialog();
                    }

                    @Override
                    public void onComplete() {
                        hideProgressDialog();

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
                //先删除本地数据库
                List<ChemicalInfo> allChemical = chemicalDao.searchAllChemical();
                if (allChemical != null) {
                    chemicalDao.deleteChemicals(allChemical);
                }

                chemicalDao.insertChemicalList(chemicalInfos);

                //文件存入数据库
                List<FileInfo> allFileBeforeList = fileDao.searchAllFile();
                if (allFileBeforeList != null) {
                    fileDao.deleteFiles(allFileBeforeList);
                }

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
                SharedPreferences sharedPreferences = getSharedPreferences(Constant
                        .MY_SHARE_PREFERENCES, MODE_PRIVATE);
                sharedPreferences.edit().putString(Constant.RELEASE_DATE, releaseDate).apply();
                return chemicalInfos;
            }
        }


        return null;
    }

    private void requestWritePermissions() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission
                .WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, getString(R.string.request_write_external),
                    REQUEST_WRITE_EXTERNAL_PERMISSIONS, perms);
        } else {
            getSystemFolder();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        getSystemFolder();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Toast.makeText(this, getString(R.string.request_write_external), Toast.LENGTH_SHORT).show();
    }


}
