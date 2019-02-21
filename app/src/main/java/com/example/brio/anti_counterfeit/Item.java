package com.example.brio.anti_counterfeit;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Item {
    private String name;
    private String description;
    private String serialNo;
    private Date mfgDate;
    private Date expDate;

    public Item() {
    }

    public Item(String name, String description, String serialNo, Date mfgDate, Date expDate) {
        this.name = name;
        this.description = description;
        this.serialNo = serialNo;
        this.mfgDate = mfgDate;
        this.expDate = expDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public Date getMfgDate() {
        return mfgDate;
    }

    public void setMfgDate(Date mfgDate) {
        this.mfgDate = mfgDate;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    @Override
    @NonNull
    public String toString() {
        SimpleDateFormat simpleDateFormat = new
                SimpleDateFormat("dd/MM/YYYY", Locale.getDefault());
        return String.format(Locale.getDefault(),
                "Name: %s\nDescription: %s\nSerial No: %s\nMfg Date: %s\nExp Date: %s",
                this.getName(),
                this.getDescription(),
                this.getSerialNo(),
                simpleDateFormat.format(this.getMfgDate()),
                simpleDateFormat.format(this.getExpDate()));
    }
}
