package com.ruobilin.basf.basfchemical.utils;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Xml;

import com.ruobilin.basf.basfchemical.bean.ChemicalInfo;
import com.ruobilin.basf.basfchemical.bean.FileInfo;
import com.ruobilin.basf.basfchemical.common.Constant;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by xingcc on 2018/12/4.
 * main function
 * 文件工具类
 *
 * @author strivecheng
 */

public class FileUtils {
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // 文档
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // 外部存储
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // 下载
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // 多媒体
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) { //图片
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) { //视频
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) { //音频
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // 4.4以下  多媒体路径
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // 文件路径
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * 获取Uri的数据列的值
     *
     * @param context       context
     * @param uri           需要查询的uri
     * @param selection     过滤条件
     * @param selectionArgs 过滤值
     * @return _data的值 一般是文件路径
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (IllegalArgumentException e) {
//            return getFilePathFromURI(context, uri);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    //判断是否为外部存储文档
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    //判断是否是下载目录中的文档
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    //判断是否是多媒体目录文档
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * 复制单个文件到指定目录
     *
     * @param fromPath
     * @param toPath
     * @return
     */
    public static boolean copyFile(String fromPath, String toPath) {
        File fromFile = new File(fromPath);
        if (!fromFile.exists()) {
            Log.e("--Method--", "copyFile:  oldFile not exist.");
            return false;
        } else if (!fromFile.isFile()) {
            Log.e("--Method--", "copyFile:  oldFile not file.");
            return false;
        } else if (!fromFile.canRead()) {
            Log.e("--Method--", "copyFile:  oldFile cannot read.");
            return false;
        }

        //目标目录
        File targetDir = new File(toPath);
        //如果父目录不存在 ，创建父目录
        if (!targetDir.getParentFile().exists()) {
            targetDir.getParentFile().mkdir();
        }

        try {
            FileInputStream fileInputStream = new FileInputStream(fromPath);    //读入原文件
            FileOutputStream fileOutputStream = new FileOutputStream(toPath);
            byte[] buffer = new byte[1024];
            int byteRead;
            while ((byteRead = fileInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除文件夹下面所有子文件
     * @param file
     */
    public static void deleteFile(File file, boolean isFirst) {
        if (file == null || !file.exists() || !file.isDirectory()) {
            return;
        }
        for (File f : file.listFiles()) {
            if (f.isFile()) {
                f.delete();
            } else if (f.isDirectory()) {
                deleteFile(f, false); // 递规的方式删除文件夹
            }
        }
        boolean b = false;// 删除目录本身
        if (!isFirst) {//第一次传进来的那一层不删除
            file.delete();
        }
//        if (file.isDirectory()) {
//            File[] files = file.listFiles();
//            Log.e("delete", "deleteFile:文件数量-- "+files.length);
//            for (File f : files) {
//                if (f.exists()) {
//                    Log.e("delete", "deleteFile:文件名-- "+f.getName());
//                    boolean delete = f.delete();
//                    if (delete){
//                        Log.e("delete", "deleteFile:删除成功 "+f.getName());
//                    }else {
//                        Log.e("delete", "deleteFile:删除失败 "+f.getName());
//                    }
//                }
//            }
//        }
    }


    /**
     * <Item Id="20180612234538006494" Name="Lupranol 2048" Code="N/A" CASNumber="N/A"
     * IsDangerous="true" CMR="N/A" HPhrase="N/A" MSDSPublicDate="2015-08-11">
     * <FileItem Name="chemical1.pdf" Path="20180612234538006494/chemical1.pdf"/>
     * <FileItem Name="chemical2.pdf" Path="20180612234538006494/chemical2.pdf"/>
     * </Item>
     * <p>
     * 解析xml
     */
    public static ArrayList<ChemicalInfo> analysisXml(File file) {
        ArrayList<ChemicalInfo> chemicalInfos = null;
        ArrayList<FileInfo> fileInfos = null;
        ChemicalInfo chemicalInfo = null;
        FileInfo fileInfo = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            // 获得pull解析器对象
            XmlPullParser parser = Xml.newPullParser();
            // 指定解析的文件和编码格式
            parser.setInput(fis, "utf-8");
            int eventType = parser.getEventType(); // 获得事件类型
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName(); // 获得当前节点的名称
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        chemicalInfos = new ArrayList<>();
                        break;
                    case XmlPullParser.START_TAG:
                        if (Constant.CHEMICAL.equals(tagName)) {
                            chemicalInfo = new ChemicalInfo();
                            chemicalInfo.setReleaseDate(parser.getAttributeValue(null, Constant.RELEASE_DATE));
                        } else if (Constant.ITEM.equals(tagName)) {

                            if (chemicalInfo == null) {
                                chemicalInfo = new ChemicalInfo();
                            }
                            if (fileInfos == null) {
                                fileInfos = new ArrayList<>();
                            }
                            chemicalInfo.setId(parser.getAttributeValue(null, Constant.ID));
                            chemicalInfo.setName(parser.getAttributeValue(null, Constant.NAME));
                            chemicalInfo.setCode(parser.getAttributeValue(null, Constant.CODE));
                            chemicalInfo.setCASNumber(parser.getAttributeValue(null, Constant.CAS_NUMBER));
                            chemicalInfo.setIsDangerous(parser.getAttributeValue(null, Constant.IS_DANGEROUS));
                            chemicalInfo.setCMR(parser.getAttributeValue(null, Constant.CMR));
                            chemicalInfo.setHPhrase(parser.getAttributeValue(null, Constant.H_PHRASE));
                            chemicalInfo.setMSDSPublicDate(parser.getAttributeValue(null, Constant.MSDS_PUBLIC_DATE));
                        } else if (Constant.FILE_ITEM.equals(tagName)) {
                            fileInfo = new FileInfo();
                            fileInfo.setId(UUID.randomUUID().toString());
                            fileInfo.setChemicalId(chemicalInfo.getId());
                            fileInfo.setName(parser.getAttributeValue(null, Constant.NAME));
                            fileInfo.setPath(parser.getAttributeValue(null, Constant.PATH));
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (Constant.ITEM.equals(tagName)) {
                            if (chemicalInfo != null) {
                                chemicalInfo.setFileInfos(fileInfos);
                                chemicalInfos.add(chemicalInfo);
                            }
                            chemicalInfo = null;
                            fileInfos = null;
                        } else if (Constant.FILE_ITEM.equals(tagName)) {
                            if (fileInfos != null) {
                                fileInfos.add(fileInfo);
                            }
                            fileInfo = null;
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chemicalInfos;
    }
}
