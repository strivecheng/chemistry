package com.ruobilin.basf.basfchemical.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ruobilin.basf.basfchemical.R;
import com.ruobilin.basf.basfchemical.bean.FileInfo;

import java.util.List;

/**
 * Created by xingcc on 2018/12/7.
 * main function
 *  文件列表适配器
 * @author strivecheng
 */

public class FileListAdapter extends BaseQuickAdapter<FileInfo,BaseViewHolder>{
    public FileListAdapter(@Nullable List<FileInfo> data) {
        super(R.layout.file_list_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FileInfo item) {
        helper.setText(R.id.file_name_tv,item.getName());
    }
}
