package com.example.infyULabs.bean;

import java.util.Date;

public class ScanBean {
    public static final String fruit_id = "id";
    public static final String scan_data = "scanData";
    public static final String scan_fruitName = "fruit";
    public static final String scan_time = "time";

    private String scanData;
    private Date scanTime;
    private String fruitName;


    public String getScanData() {
        return scanData;
    }

    @Override
    public String toString() {
        return "ScanBean{" +
                "scanData='" + scanData + '\'' +
                ", scanTime='" + scanTime + '\'' +
                ", fruitName='" + fruitName + '\'' +
                '}';
    }

    public void setScanData(String scanData) {
        this.scanData = scanData;
    }

    public Date getScanTime() {
        return scanTime;
    }

    public void setScanTime(Date scanTime) {
        this.scanTime = scanTime;
    }

    public String getFruitName() {
        return fruitName;
    }

    public void setFruitName(String fruitName) {
        this.fruitName = fruitName;
    }
}
