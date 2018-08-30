package com.dynamsoft.demo.dynamsoftbarcodereaderdemo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.adapter.HistoryListAdapter;

import cn.bingoogolapple.baseadapter.BGAOnItemChildClickListener;

public class BaseFragment extends Fragment implements BGAOnItemChildClickListener{
    @Override
    public void onItemChildClick(ViewGroup parent, View childView, int position) {

    }

}
