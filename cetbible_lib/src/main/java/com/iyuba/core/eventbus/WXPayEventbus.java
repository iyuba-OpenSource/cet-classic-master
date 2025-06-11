package com.iyuba.core.eventbus;

public class WXPayEventbus {
    private int errCode;


    public WXPayEventbus(int errCode) {
        this.errCode = errCode;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }
}
