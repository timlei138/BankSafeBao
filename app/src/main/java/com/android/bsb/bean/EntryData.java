package com.android.bsb.bean;

public class EntryData {

    private String label;

    private float data;

    public EntryData(){

    }

    public EntryData(String label,float data){
        this.label = label;
        this.data = data;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public float getData() {
        return data;
    }

    public void setData(float data) {
        this.data = data;
    }
}
