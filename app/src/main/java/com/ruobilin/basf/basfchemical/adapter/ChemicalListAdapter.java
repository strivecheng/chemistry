package com.ruobilin.basf.basfchemical.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ruobilin.basf.basfchemical.R;
import com.ruobilin.basf.basfchemical.bean.ChemicalInfo;

import java.util.List;

/**
 * Created by xingcc on 2018/12/5.
 * main function
 *
 * @author strivecheng
 */

public class ChemicalListAdapter extends BaseQuickAdapter<ChemicalInfo,BaseViewHolder> {
    public ChemicalListAdapter(@Nullable List<ChemicalInfo> data) {
        super(R.layout.chemical_list_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ChemicalInfo item) {
        helper.setText(R.id.name_tv,item.getName())
                .setText(R.id.code_tv,item.getCode())
                .setText(R.id.number_tv, mContext.getString(R.string.cas_number)+":"+item.getCASNumber())
                .setText(R.id.date_tv,mContext.getString(R.string.release_date)+":"+item.getMSDSPublicDate())
        .addOnClickListener(R.id.m_chemical_list_card_view);
    }
}
