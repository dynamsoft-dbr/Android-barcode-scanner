package com.dynamsoft.demo.dynamsoftbarcodereaderdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.dynamsoft.barcode.jni.BarcodeReader;
import com.dynamsoft.barcode.jni.TextResult;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DecodeLocalPictureActivity extends AppCompatActivity {

    @BindView(R.id.iv_detail)
    ImageView ivDetail;
    @BindView(R.id.pb_progress)
    ProgressBar progressBar;
    private BarcodeReader reader;
    private String filePath;
    private final int BARCODE_RESULT = 0x0001;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message){
            super.handleMessage(message);
            switch (message.what){
                case BARCODE_RESULT:
                    showResults((TextResult[])message.obj);
                    progressBar.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decode_local_picture);
        ButterKnife.bind(this);
        progressBar.setVisibility(View.VISIBLE);
        filePath = this.getIntent().getStringExtra("FilePath");
        Glide.with(this).load(filePath).into(ivDetail);
        initBarcodeReader();
        decode();
    }

    private void initBarcodeReader() {
        try {
            reader = new BarcodeReader(getString(R.string.dbr_license));
            JSONObject jsonObject = new JSONObject("{\n" +
                    "  \"ImageParameters\": {\n" +
                    "    \"Name\": \"Custom_100947_777\",\n" +
                    "    \"BarcodeFormatIds\": [\n" +
                    "      \"QR_CODE\"\n" +
                    "    ],\n" +
                    "    \"LocalizationAlgorithmPriority\": [\"ConnectedBlock\", \"Lines\", \"Statistics\", \"FullImageAsBarcodeZone\"],\n" +
                    "    \"AntiDamageLevel\": 5,\n" +
                    "    \"DeblurLevel\":5,\n" +
                    "    \"ScaleDownThreshold\": 1000\n" +
                    "  }\n" +
                    "}");
            reader.appendParameterTemplate(jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void decode(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BarcodeReader reader = new BarcodeReader("f0068MgAAAB1lHa1TS73f6hvSsyG9UkU+EITa8w0074QekQD7/go" +
                            "xYCguWUiLgYMKRg4ta39gsM08V5J5F3H0l6puHcJ0Yso=");
                    InputStream inputStream = new FileInputStream(filePath);
                    byte[] bytes = new byte[inputStream.available()];
                    inputStream.read(bytes);
                    inputStream.close();
                    TextResult[] results = reader.decodeFileInMemory(bytes, "");
                    Message message = handler.obtainMessage();
                    message.obj = results;
                    message.what = BARCODE_RESULT;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void showResults(TextResult[] results){
        final AlertDialog.Builder builder = new AlertDialog.Builder(DecodeLocalPictureActivity.this)
                .setTitle("Barcode Result");
        String resultMessage = "Total barcode(s) found: " + String.valueOf(results.length) + "\n";
        String barcodeFormat = "";
        for (int i = 0; i < results.length; i++) {
            if (results[i].localizationResult != null &&
                    results[i].localizationResult.resultPoints != null &&
                    results[i].localizationResult.resultPoints.length > 0) {

                int x0 = results[i].localizationResult.resultPoints[0].x;
                int y0 = results[i].localizationResult.resultPoints[0].y;
                int x1 = results[i].localizationResult.resultPoints[1].x;
                int y1 = results[i].localizationResult.resultPoints[1].y;
                int x2 = results[i].localizationResult.resultPoints[2].x;
                int y2 = results[i].localizationResult.resultPoints[2].y;
                int x3 = results[i].localizationResult.resultPoints[3].x;
                int y3 = results[i].localizationResult.resultPoints[3].y;
                int[] xAarray = new int[]{x0, x1, x2, x3};
                int[] yAarray = new int[]{y0, y1, y2, y3};
                Arrays.sort(xAarray);
                Arrays.sort(yAarray);
                switch (results[i].barcodeFormat) {
                    case 234882047:
                        barcodeFormat = "all";
                        break;
                    case 1023:
                        barcodeFormat = "OneD";
                        break;
                    case 1:
                        barcodeFormat = "CODE_39";
                        break;
                    case 2:
                        barcodeFormat = "CODE_128";
                        break;
                    case 4:
                        barcodeFormat = "CODE_93";
                        break;
                    case 8:
                        barcodeFormat = "CODABAR";
                        break;
                    case 16:
                        barcodeFormat = "ITF";
                        break;
                    case 32:
                        barcodeFormat = "EAN_13";
                        break;
                    case 64:
                        barcodeFormat = "EAN_8";
                        break;
                    case 128:
                        barcodeFormat = "UPC_A";
                        break;
                    case 256:
                        barcodeFormat = "UPC_E";
                        break;
                    case 512:
                        barcodeFormat = "INDUSTRIAL_25";
                        break;
                    case 33554432:
                        barcodeFormat = "PDF417";
                        break;
                    case 67108864:
                        barcodeFormat = "QR_CODE";
                        break;
                    case 134217728:

                        barcodeFormat = "DATAMATAIX";
                        break;
                    default:
                        break;
                }
                resultMessage += "Barcode : " + String.valueOf(i + 1) + "\nType : " + barcodeFormat + "\nResult : " + results[i].barcodeText + "\nRegion : {Left : " + xAarray[0]
                        + " Top : " + yAarray[0] + " Right : " + xAarray[3] + " Bottom : " + yAarray[3]
                        + "}\n\n";
            }
        }
        builder.setMessage(resultMessage);
        builder.show();
    }

}
