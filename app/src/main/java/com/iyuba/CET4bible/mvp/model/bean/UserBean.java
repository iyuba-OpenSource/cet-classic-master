package com.iyuba.CET4bible.mvp.model.bean;

import com.google.gson.annotations.SerializedName;

public class UserBean {

    @SerializedName("isLogin")
    private String isLogin;
    @SerializedName("res")
    private ResDTO res;
    @SerializedName("userinfo")
    private UserinfoDTO userinfo;

    public String getIsLogin() {
        return isLogin;
    }

    public void setIsLogin(String isLogin) {
        this.isLogin = isLogin;
    }

    public ResDTO getRes() {
        return res;
    }

    public void setRes(ResDTO res) {
        this.res = res;
    }

    public UserinfoDTO getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(UserinfoDTO userinfo) {
        this.userinfo = userinfo;
    }

    public static class ResDTO {
        @SerializedName("valid")
        private boolean valid;
        @SerializedName("phone")
        private String phone;
        @SerializedName("isValid")
        private int isValid;

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public int getIsValid() {
            return isValid;
        }

        public void setIsValid(int isValid) {
            this.isValid = isValid;
        }
    }

    public static class UserinfoDTO {
        @SerializedName("uid")
        private int uid;
        @SerializedName("expireTime")
        private long expireTime;
        @SerializedName("result")
        private String result;
        @SerializedName("Amount")
        private String amount;
        @SerializedName("vipStatus")
        private String vipStatus;
        @SerializedName("nickname")
        private String nickname;
        @SerializedName("credits")
        private int credits;
        @SerializedName("message")
        private String message;
        @SerializedName("username")
        private String username;
        @SerializedName("email")
        private String email;
        @SerializedName("jiFen")
        private int jiFen;
        @SerializedName("imgSrc")
        private String imgSrc;
        @SerializedName("money")
        private String money;
        @SerializedName("mobile")
        private String mobile;
        @SerializedName("isteacher")
        private String isteacher;


        public boolean isVip() {

            if (vipStatus != null && !vipStatus.equals("0")) {

                long et = 0;
                if ((expireTime + "").length() == 10) {
                    et = expireTime * 1000;
                } else {
                    et = expireTime;
                }

                if (System.currentTimeMillis() > et) {

                    return false;
                } else {

                    return true;
                }
            } else {

                return false;
            }
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public long getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(long expireTime) {
            this.expireTime = expireTime;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getVipStatus() {
            return vipStatus;
        }

        public void setVipStatus(String vipStatus) {
            this.vipStatus = vipStatus;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public int getCredits() {
            return credits;
        }

        public void setCredits(int credits) {
            this.credits = credits;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public int getJiFen() {
            return jiFen;
        }

        public void setJiFen(int jiFen) {
            this.jiFen = jiFen;
        }

        public String getImgSrc() {
            return imgSrc;
        }

        public void setImgSrc(String imgSrc) {
            this.imgSrc = imgSrc;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getIsteacher() {
            return isteacher;
        }

        public void setIsteacher(String isteacher) {
            this.isteacher = isteacher;
        }
    }
}
