package com.dynamsoft.demo.dynamsoftbarcodereaderdemo.adapter;

import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.R;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.DBRImage;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.HistoryItemBean;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;

/**
 * Created by Elemen on 2018/7/3.
 */
public class HistoryListAdapter extends BGARecyclerViewAdapter<DBRImage> {
	public HistoryListAdapter(RecyclerView recyclerView) {
		super(recyclerView, R.layout.history_recyclerview_item);
	}

	@Override
	protected void setItemChildListener(BGAViewHolderHelper helper, int viewType) {
		super.setItemChildListener(helper, viewType);
		helper.setItemChildClickListener(R.id.cl_item_history);
	}

	@Override
	protected void fillData(BGAViewHolderHelper helper, int position, DBRImage model) {
		Glide.with(mContext).load(model.getCodeImgPath()).into(helper.getImageView(R.id.iv_codeimg));
		helper.setText(R.id.tv_codeformat, model.getFileName());
		long modifyDate = new File(model.getCodeImgPath()).lastModified();
		SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date = new Date(modifyDate);
			helper.setText(R.id.tv_codetext, formatter.format(date));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
