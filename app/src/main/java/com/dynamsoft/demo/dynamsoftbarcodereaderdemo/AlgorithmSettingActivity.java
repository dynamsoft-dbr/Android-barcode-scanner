package com.dynamsoft.demo.dynamsoftbarcodereaderdemo;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.util.DBRCache;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlgorithmSettingActivity extends AppCompatActivity {
    private List<String> algorithmSetting = new ArrayList<>();
    private DBRCache mCache;
    private String localizationAlgorithmPriority = "";
    @BindView(R.id.ckbconnectedblock)
    CheckBox ckbConnectedBlock;
    @BindView(R.id.tvconnectedblock)
    TextView tvConnectedblock;
    @BindView(R.id.ckblines)
    CheckBox ckbLines;
    @BindView(R.id.tvlines)
    TextView tvLines;
    @BindView(R.id.ckbstatistics)
    CheckBox ckbStatistics;
    @BindView(R.id.tvstatistics)
    TextView tvStatistics;
    @BindView(R.id.ckbfullimageasbarcodezone)
    CheckBox ckbFullImageAsBarcodeZone;
    @BindView(R.id.tvfullimageasbarcodezone)
    TextView tvFullImageAsBarcodeZone;
    @BindView(R.id.algorithmtoolbar)
    Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_algorithm_setting);
        ButterKnife.bind(this);
        mCache = DBRCache.get(this, "SettingCache");
        ckbConnectedBlock.setOnCheckedChangeListener(checkedChangeListener);
        ckbLines.setOnCheckedChangeListener(checkedChangeListener);
        ckbStatistics.setOnCheckedChangeListener(checkedChangeListener);
        ckbFullImageAsBarcodeZone.setOnCheckedChangeListener(checkedChangeListener);
        if (mCache.getAsString("LocalizationAlgorithmPriority") != null) {
            List<String> localSetting = Arrays.asList( mCache.getAsString("LocalizationAlgorithmPriority").split(","));
            for (int i = 0; i < localSetting.size(); i++){
                if(localSetting.get(i).equals(getResources().getString(R.string.connectedblock))){
                    ckbConnectedBlock.setChecked(true);
                }else if(localSetting.get(i).equals(getResources().getString(R.string.lines))){
                    ckbLines.setChecked(true);
                }else if(localSetting.get(i).equals(getResources().getString(R.string.statistics))){
                    ckbStatistics.setChecked(true);
                }else if(localSetting.get(i).equals(getResources().getString(R.string.fullimageasbarcodezone))){
                    ckbFullImageAsBarcodeZone.setChecked(true);
                }
            }
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()){
                case R.id.ckbconnectedblock:
                    if(isChecked) {
                        algorithmSetting.add(getResources().getString(R.string.connectedblock));
                    }else{
                        algorithmSetting.remove(getResources().getString(R.string.connectedblock));
                    }
                    check(algorithmSetting);
                    break;
                case R.id.ckblines:
                    if(isChecked) {
                        algorithmSetting.add(getResources().getString(R.string.lines));
                    }else{
                        algorithmSetting.remove(getResources().getString(R.string.lines));
                    }
                    check(algorithmSetting);
                    break;
                case R.id.ckbstatistics:
                    if(isChecked) {
                        algorithmSetting.add(getResources().getString(R.string.statistics));
                    }else {
                        algorithmSetting.remove(getResources().getString(R.string.statistics));
                    }
                    check(algorithmSetting);
                    break;
                case R.id.ckbfullimageasbarcodezone:
                    if (isChecked) {
                        algorithmSetting.add(getResources().getString(R.string.fullimageasbarcodezone));
                    }else {
                        algorithmSetting.remove(getResources().getString(R.string.fullimageasbarcodezone));
                    }
                    check(algorithmSetting);
                    break;
                default:
                    break;
            }
        }
    };
    private void check(List<String> arrayList){
        if (arrayList.size() > 0) {
            for (int i = 0; i < arrayList.size(); i++){
                if (arrayList.get(i).equals(getResources().getString(R.string.connectedblock))){
                    tvConnectedblock.setText(String.valueOf(i + 1));
                }else if (arrayList.get(i).equals(getResources().getString((R.string.lines)))){
                    tvLines.setText(String.valueOf(i + 1));
                }else if (arrayList.get(i).equals(getResources().getString((R.string.statistics)))){
                    tvStatistics.setText(String.valueOf(i + 1));
                }else {
                    tvFullImageAsBarcodeZone.setText(String.valueOf(i + 1));
                }
            }
        }
        if (!arrayList.contains(getResources().getString(R.string.connectedblock))){
            tvConnectedblock.setText("");
        }
        if (!arrayList.contains(getResources().getString(R.string.lines))){
            tvLines.setText("");
        }
        if (!arrayList.contains(getResources().getString(R.string.statistics))){
            tvStatistics.setText("");
        }
        if (!arrayList.contains(getResources().getString(R.string.fullimageasbarcodezone))){
            tvFullImageAsBarcodeZone.setText("");
        }
    }
    @Override
    public void onBackPressed(){
        for(String t : algorithmSetting){
            localizationAlgorithmPriority = localizationAlgorithmPriority + t + ",";
        }
        mCache.put("LocalizationAlgorithmPriority", localizationAlgorithmPriority);
        super.onBackPressed();
    }
}
