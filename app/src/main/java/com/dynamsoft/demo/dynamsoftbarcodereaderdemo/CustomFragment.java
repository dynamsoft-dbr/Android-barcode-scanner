package com.dynamsoft.demo.dynamsoftbarcodereaderdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.adapter.HistoryListAdapter;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.DBRImage;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.util.DBRCache;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bingoogolapple.baseadapter.BGADivider;
import cn.bingoogolapple.baseadapter.BGAOnItemChildClickListener;

/**
 * Created by Elemen on 2018/8/29.
 */
public class CustomFragment extends BaseFragment {
    private RecyclerView rlvHistory;
    private ProgressBar progressBar;
    private HistoryActivity historyActivity;
    private ImageView ivEmpty;
    private TextView tvNoFiles;
    private TextView tvStart;
    private Button btnAddNow;

    private HistoryListAdapter historyListAdapter;
    private List<DBRImage> imageList;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (imageList != null && imageList.size() > 0) {
                historyListAdapter.clear();
                historyListAdapter.notifyDataSetChanged();
            }
            progressBar.setVisibility(View.GONE);
            btnAddNow.setVisibility(View.VISIBLE);
            tvStart.setVisibility(View.VISIBLE);
            tvNoFiles.setVisibility(View.VISIBLE);
            ivEmpty.setVisibility(View.VISIBLE);
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history_item, null);
        rlvHistory = v.findViewById(R.id.rl_history);
        progressBar = v.findViewById(R.id.pb_progress);
        ivEmpty = v.findViewById(R.id.iv_empty);
        tvNoFiles = v.findViewById(R.id.tv_no_files);
        tvStart = v.findViewById(R.id.tv_start);
        btnAddNow = v.findViewById(R.id.btn_add_now);
        btnAddNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBRCache mCache = DBRCache.get(getActivity(), "SettingCache");
                mCache.put("templateType", "CustomSetting");
                startActivity(new Intent(getActivity(), SettingActivity.class));
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        historyListAdapter = new HistoryListAdapter(rlvHistory);
        historyListAdapter.setOnItemChildClickListener(this);
        fillHistoryList();
    }

    @Override
    public void onResume() {
        imageList = LitePal.where("templateType = ?", "CustomSetting").find(DBRImage.class);
        Collections.reverse(imageList);
        if (imageList.size() > 16) {
            imageList = imageList.subList(0, 16);
        }
        if (imageList.isEmpty()){
            btnAddNow.setVisibility(View.VISIBLE);
            tvStart.setVisibility(View.VISIBLE);
            tvNoFiles.setVisibility(View.VISIBLE);
            ivEmpty.setVisibility(View.VISIBLE);
        } else {
            btnAddNow.setVisibility(View.GONE);
            tvStart.setVisibility(View.GONE);
            tvNoFiles.setVisibility(View.GONE);
            ivEmpty.setVisibility(View.GONE);
        }
        historyListAdapter.setData(imageList);
        historyListAdapter.notifyDataSetChanged();
        super.onResume();
    }

    private void fillHistoryList() {
        imageList = LitePal.where("templateType = ?", "CustomSetting").find(DBRImage.class);
        Collections.reverse(imageList);
        if (imageList.size() > 16) {
            imageList = imageList.subList(0, 16);
        }
        if (imageList.isEmpty()){
            btnAddNow.setVisibility(View.VISIBLE);
            tvStart.setVisibility(View.VISIBLE);
            tvNoFiles.setVisibility(View.VISIBLE);
            ivEmpty.setVisibility(View.VISIBLE);
        } else {
            btnAddNow.setVisibility(View.GONE);
            tvStart.setVisibility(View.GONE);
            tvNoFiles.setVisibility(View.GONE);
            ivEmpty.setVisibility(View.GONE);
        }
        historyListAdapter.setData(imageList);
        rlvHistory.addItemDecoration(BGADivider.newShapeDivider());
        rlvHistory.setLayoutManager(getLinearLayoutManager());
        rlvHistory.setAdapter(historyListAdapter);
    }
    public void clearHistoryList() {
        progressBar.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<DBRImage> allImageList = LitePal.findAll(DBRImage.class);
                for (DBRImage dbrImage : allImageList) {
                    if ("GeneralSetting".equals(dbrImage.getTemplateType())) {
                        imageList.add(dbrImage);
                    }
                }
                if (imageList != null && imageList.size() > 0) {
                    String path = Environment.getExternalStorageDirectory() + "/dbr-preview-img";
                    for (int i = 0; i < imageList.size(); i++){
                        File temp = new File(path, imageList.get(i).getFileName() + ".jpg");
                        if(temp.exists()){
                            temp.delete();
                        }
                    }
                    LitePal.deleteAll(DBRImage.class, "templateType = ?", "CustomSetting");
                }
                handler.sendEmptyMessage(0);
            }
        }).start();
    }
    private RecyclerView.LayoutManager getLinearLayoutManager() {
        return new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    }
    @Override
    public void onItemChildClick(ViewGroup parent, View childView, int position) {
        switch (childView.getId()) {
            case R.id.cl_item_history:
                Intent intent = new Intent(getActivity(), HistoryItemDetailActivity.class);
                intent.putExtra("page_type", 1);
                intent.putExtra("position", position);
                intent.putExtra("templateType", "CustomSetting");
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
