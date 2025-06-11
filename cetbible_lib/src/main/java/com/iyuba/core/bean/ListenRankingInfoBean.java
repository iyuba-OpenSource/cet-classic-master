package com.iyuba.core.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by 15730 on 2018/4/17.
 */

public class ListenRankingInfoBean {


    @SerializedName("result")
    private int result;
    @SerializedName("myimgSrc")
    private String myimgSrc;
    @SerializedName("myid")
    private int myid;
    @SerializedName("myranking")
    private int myranking;
    @SerializedName("data")
    private List<DataDTO> data;
    @SerializedName("totalTime")
    private int totalTime;
    @SerializedName("totalWord")
    private int totalWord;
    @SerializedName("myname")
    private String myname;
    @SerializedName("totalEssay")
    private int totalEssay;
    @SerializedName("message")
    private String message;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMyimgSrc() {
        return myimgSrc;
    }

    public void setMyimgSrc(String myimgSrc) {
        this.myimgSrc = myimgSrc;
    }

    public int getMyid() {
        return myid;
    }

    public void setMyid(int myid) {
        this.myid = myid;
    }

    public int getMyranking() {
        return myranking;
    }

    public void setMyranking(int myranking) {
        this.myranking = myranking;
    }

    public List<DataDTO> getData() {
        return data;
    }

    public void setData(List<DataDTO> data) {
        this.data = data;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public int getTotalWord() {
        return totalWord;
    }

    public void setTotalWord(int totalWord) {
        this.totalWord = totalWord;
    }

    public String getMyname() {
        return myname;
    }

    public void setMyname(String myname) {
        this.myname = myname;
    }

    public int getTotalEssay() {
        return totalEssay;
    }

    public void setTotalEssay(int totalEssay) {
        this.totalEssay = totalEssay;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class DataDTO {
        @SerializedName("uid")
        private int uid;
        @SerializedName("totalTime")
        private int totalTime;
        @SerializedName("totalWord")
        private int totalWord;
        @SerializedName("name")
        private String name;
        @SerializedName("ranking")
        private int ranking;
        @SerializedName("sort")
        private int sort;
        @SerializedName("totalEssay")
        private int totalEssay;
        @SerializedName("imgSrc")
        private String imgSrc;

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public int getTotalTime() {
            return totalTime;
        }

        public void setTotalTime(int totalTime) {
            this.totalTime = totalTime;
        }

        public int getTotalWord() {
            return totalWord;
        }

        public void setTotalWord(int totalWord) {
            this.totalWord = totalWord;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getRanking() {
            return ranking;
        }

        public void setRanking(int ranking) {
            this.ranking = ranking;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public int getTotalEssay() {
            return totalEssay;
        }

        public void setTotalEssay(int totalEssay) {
            this.totalEssay = totalEssay;
        }

        public String getImgSrc() {
            return imgSrc;
        }

        public void setImgSrc(String imgSrc) {
            this.imgSrc = imgSrc;
        }
    }
}
