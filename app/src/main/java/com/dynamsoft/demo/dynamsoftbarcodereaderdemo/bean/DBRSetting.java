package com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.ArrayList;

import javax.xml.transform.Templates;

/**
 * Created by Martin on 8/1/2018.
 */
@JsonObject
public class DBRSetting {
    @JsonField
    private String templateName = "Custom";
    @JsonField
    private ArrayList<String> barcodeFormat = new ArrayList<>();
    @JsonField
    private int expectedBarcodesCount = 0;
    @JsonField
    private int timeout = 10000;
    @JsonField
    private int deblurLevel = 9;
    @JsonField
    private int antiDamageLevel = 9;
    @JsonField
    private boolean textFilterMode = true;
    @JsonField
    private boolean regionPredetectionMode = false;
    @JsonField
    private int scaleDownThreshold = 2300;
    @JsonField
    private String colourImageConvertMode = "Auto";
    @JsonField
    private String barcodeInvertMode = "DarkOnLight";
    @JsonField
    private int grayEqualizationSensitivity = 0;
    @JsonField
    private int textureDetectionSensitivity = 5;
    @JsonField
    private int binarizationBlockSize = 0;
    @JsonField
    private String localizationAlgorithmPriority = null;
    @JsonField
    private int maxDimOfFullImageAsBarcodeZone = 262144;
    @JsonField
    private int minImageDimensionToPredetectRegion = 262144;
    @JsonField
    private long maxBarcodesCount = 22147483647L;
    @JsonField
    private boolean enableFillBinaryVacancy = true;
    @JsonField
    private float version = 2.0f;

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public ArrayList<String> getBarcodeFormat() {
        return barcodeFormat;
    }

    public void setBarcodeFormat(ArrayList<String> barcodeFormat) {
        this.barcodeFormat = barcodeFormat;
    }

    public int getExpectedBarcodesCount() {
        return expectedBarcodesCount;
    }

    public void setExpectedBarcodesCount(int expectedBarcodesCount) {
        this.expectedBarcodesCount = expectedBarcodesCount;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getDeblurLevel() {
        return deblurLevel;
    }

    public void setDeblurLevel(int deblurLevel) {
        this.deblurLevel = deblurLevel;
    }

    public int getAntiDamageLevel() {
        return antiDamageLevel;
    }

    public void setAntiDamageLevel(int antiDamageLevel) {
        this.antiDamageLevel = antiDamageLevel;
    }

    public boolean isTextFilterMode() {
        return textFilterMode;
    }

    public void setTextFilterMode(boolean textFilterMode) {
        this.textFilterMode = textFilterMode;
    }

    public boolean isRegionPredetectionMode() {
        return regionPredetectionMode;
    }

    public void setRegionPredetectionMode(boolean regionPredetectionMode) {
        this.regionPredetectionMode = regionPredetectionMode;
    }

    public int getScaleDownThreshold() {
        return scaleDownThreshold;
    }

    public void setScaleDownThreshold(int scaleDownThreshold) {
        this.scaleDownThreshold = scaleDownThreshold;
    }

    public String getColourImageConvertMode() {
        return colourImageConvertMode;
    }

    public void setColourImageConvertMode(String colourImageConvertMode) {
        this.colourImageConvertMode = colourImageConvertMode;
    }

    public String getBarcodeInvertMode() {
        return barcodeInvertMode;
    }

    public void setBarcodeInvertMode(String barcodeInvertMode) {
        this.barcodeInvertMode = barcodeInvertMode;
    }

    public int getGrayEqualizationSensitivity() {
        return grayEqualizationSensitivity;
    }

    public void setGrayEqualizationSensitivity(int grayEqualizationSensitivity) {
        this.grayEqualizationSensitivity = grayEqualizationSensitivity;
    }

    public int getTextureDetectionSensitivity() {
        return textureDetectionSensitivity;
    }

    public void setTextureDetectionSensitivity(int textureDetectionSensitivity) {
        this.textureDetectionSensitivity = textureDetectionSensitivity;
    }

    public int getBinarizationBlockSize() {
        return binarizationBlockSize;
    }

    public void setBinarizationBlockSize(int binarizationBlockSize) {
        this.binarizationBlockSize = binarizationBlockSize;
    }

    public String getLocalizationAlgorithmPriority() {
        return localizationAlgorithmPriority;
    }

    public void setLocalizationAlgorithmPriority(String localizationAlgorithmPriority) {
        this.localizationAlgorithmPriority = localizationAlgorithmPriority;
    }

    public int getMaxDimOfFullImageAsBarcodeZone() {
        return maxDimOfFullImageAsBarcodeZone;
    }

    public void setMaxDimOfFullImageAsBarcodeZone(int maxDimOfFullImageAsBarcodeZone) {
        this.maxDimOfFullImageAsBarcodeZone = maxDimOfFullImageAsBarcodeZone;
    }

    public int getMinImageDimensionToPredetectRegion() {
        return minImageDimensionToPredetectRegion;
    }

    public void setMinImageDimensionToPredetectRegion(int minImageDimensionToPredetectRegion) {
        this.minImageDimensionToPredetectRegion = minImageDimensionToPredetectRegion;
    }

    public long getMaxBarcodesCount() {
        return maxBarcodesCount;
    }

    public void setMaxBarcodesCount(long maxBarcodesCount) {
        this.maxBarcodesCount = maxBarcodesCount;
    }

    public boolean isEnableFillBinaryVacancy() {
        return enableFillBinaryVacancy;
    }

    public void setEnableFillBinaryVacancy(boolean enableFillBinaryVacancy) {
        this.enableFillBinaryVacancy = enableFillBinaryVacancy;
    }

    public float getVersion() {
        return version;
    }

    public void setVersion(float version) {
        this.version = version;
    }
}
