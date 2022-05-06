package com.example.bkgreenhouse;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class DataGroup {
    private ArrayList<Float> mList;
    private ArrayList<Float> nList;
    private ArrayList<Float> eList;
    private float mAverage;
    private float nAverage;
    private float eAverage;

    public DataGroup() {
        this.mList = new ArrayList<>();
        this.nList = new ArrayList<>();
        this.eList = new ArrayList<>();
        this.mAverage = 0;
        this.nAverage = 0;
        this.eAverage = 0;
    }

    public float getmAverage() {
        return mAverage;
    }

    public float getnAverage() {
        return nAverage;
    }

    public float geteAverage() {
        return eAverage;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addDataPoint(Float value, String createAt){
        int time = Integer.parseInt(createAt.substring(0,2));
        if (time < 4){
            eList.add(value);
            calAverageE();
        } else if (time < 12){
            mList.add(value);
            calAverageM();
        } else if (time < 20){
            nList.add(value);
            calAverageN();
        } else {
            eList.add(value);
            calAverageE();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void calAverageE() {
        eAverage = (float) eList.stream().mapToDouble(a -> a).average().orElse(0);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void calAverageM() {
        mAverage = (float) mList.stream().mapToDouble(a -> a).average().orElse(0);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void calAverageN() {
        nAverage = (float) nList.stream().mapToDouble(a -> a).average().orElse(0);
    }

    public float getValueOf(int index){
        switch (index){
            case 1: return this.mAverage;
            case 2: return this.nAverage;
            case 3: return this.eAverage;
            default: return 0;
        }
    }
}
