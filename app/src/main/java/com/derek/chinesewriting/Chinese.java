package com.derek.chinesewriting;

import android.support.annotation.IdRes;

public class Chinese {

    private @IdRes int idRes;
    private @IdRes int idResBack;
    private float rate;

    public Chinese(int idRes) {
        this.idRes = idRes;
    }

    public Chinese(int idRes, int idResBack, float rate) {
        this.idRes = idRes;
        this.idResBack = idResBack;
        this.rate = rate;
    }

    public int getIdRes() {
        return idRes;
    }

    public void setIdRes(int idRes) {
        this.idRes = idRes;
    }

    public int getIdResBack() {
        return idResBack;
    }

    public void setIdResBack(int idResBack) {
        this.idResBack = idResBack;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }
}
