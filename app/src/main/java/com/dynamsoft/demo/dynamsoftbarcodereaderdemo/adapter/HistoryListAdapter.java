package com.dynamsoft.demo.dynamsoftbarcodereaderdemo.adapter;

import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.R;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.HistoryItemBean;

import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;

/**
 * Created by Elemen on 2018/7/3.
 */
public class HistoryListAdapter extends BGARecyclerViewAdapter<HistoryItemBean> {
	public HistoryListAdapter(RecyclerView recyclerView) {
		super(recyclerView, R.layout.history_recyclerview_item);
	}

	@Override
	protected void setItemChildListener(BGAViewHolderHelper helper, int viewType) {
		super.setItemChildListener(helper, viewType);
	}

	@Override
	protected void fillData(BGAViewHolderHelper helper, int position, HistoryItemBean model) {
		Glide.with(mContext).load(model.codeImgPath).into(helper.getImageView(R.id.iv_codeimg));
		helper.setText(R.id.tv_codeformat, model.codeFormat);
		helper.setText(R.id.tv_codetext, model.codeText);
	}
}