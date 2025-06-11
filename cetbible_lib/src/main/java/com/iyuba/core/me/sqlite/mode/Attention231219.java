package com.iyuba.core.me.sqlite.mode;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Attention231219 {


    @SerializedName("result")
    private String result;
    @SerializedName("data")
    private List<DataDTO> data;
    @SerializedName("num")
    private int num;
    @SerializedName("message")
    private String message;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<DataDTO> getData() {
        return data;
    }

    public void setData(List<DataDTO> data) {
        this.data = data;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class DataDTO {
        @SerializedName("fusername")
        private String fusername;
        @SerializedName("doing")
        private String doing;
        @SerializedName("mutual")
        private String mutual;
        @SerializedName("dateline")
        private String dateline;
        @SerializedName("followuid")
        private String followuid;
        @SerializedName("vip")
        private String vip;
        @SerializedName("status")
        private String status;

        public String getFusername() {
            return fusername;
        }

        public void setFusername(String fusername) {
            this.fusername = fusername;
        }

        public String getDoing() {
            return doing;
        }

        public void setDoing(String doing) {
            this.doing = doing;
        }

        public String getMutual() {
            return mutual;
        }

        public void setMutual(String mutual) {
            this.mutual = mutual;
        }

        public String getDateline() {
            return dateline;
        }

        public void setDateline(String dateline) {
            this.dateline = dateline;
        }

        public String getFollowuid() {
            return followuid;
        }

        public void setFollowuid(String followuid) {
            this.followuid = followuid;
        }

        public String getVip() {
            return vip;
        }

        public void setVip(String vip) {
            this.vip = vip;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
