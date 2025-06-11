package com.iyuba.core.mvp.model.bean;

import com.google.gson.annotations.SerializedName;

/**
 *
 *     albums = 0;//专辑数
 *     allThumbUp = 552;//总的点赞数
 *     bio = "是啊真的好想好好学学吧”;//一句话简介
 *     blogs = 0;//日志数
 *     contribute = 0;
 *     credits = 10304;//积分数
 *     distance = "";
 *     doings = 47;//说说数目
 *     email = "chenjinrong@iyuba.cn”;//注册邮箱
 *     follower = 222;//粉丝数
 *     following = 50;//关注数
 *     friends = 0;
 *     gender = 1;//性别
 *     icoins = 10304;//积分
 *     message = "";
 *     //http://static1.iyuba.cn/uc_server/
 *     "middle_url" = "head/2021/2/5/19/3/52/59f71a19-77b9-4093-ae47-9781fc1bb014-m.jpg”;//头像
 *
 *     posts = 0;
 *     relation = 0;
 *     result = 201;
 *     sharings = 0;
 *     shengwang = 0;//声望数
 *     text = "good morning”;//最近的一个说说文字内容
 *     views = 27571;//多少人看我的主页
 *
 *     vipStatus = 1;//会员状态
 *     username = Jinrong110;//用户名
 *     money = 315;//钱包, 单位是分
 *     nickname = "每个月”;//昵称
 *     mobile = 00000000000;//手机号
 *     expireTime = 1689400894;//会员到期时间
 *     email = "chenjinrong@iyuba.cn”;//注册邮箱
 *     credits = 10304;//积分数
 *     amount = 24;//爱语币数量
 */
public class MoreInfoBean {


    @SerializedName("albums")
    private String albums;
    @SerializedName("gender")
    private String gender;
    @SerializedName("distance")
    private String distance;
    @SerializedName("blogs")
    private String blogs;
    @SerializedName("middle_url")
    private String middleUrl;
    @SerializedName("contribute")
    private String contribute;
    @SerializedName("shengwang")
    private String shengwang;
    @SerializedName("bio")
    private String bio;
    @SerializedName("posts")
    private String posts;
    @SerializedName("relation")
    private int relation;
    @SerializedName("result")
    private int result;
    @SerializedName("isteacher")
    private String isteacher;
    @SerializedName("credits")
    private String credits;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("email")
    private String email;
    @SerializedName("views")
    private String views;
    @SerializedName("amount")
    private int amount;
    @SerializedName("follower")
    private int follower;
    @SerializedName("mobile")
    private String mobile;
    @SerializedName("allThumbUp")
    private String allThumbUp;
    @SerializedName("icoins")
    private String icoins;
    @SerializedName("message")
    private String message;
    @SerializedName("friends")
    private String friends;
    @SerializedName("doings")
    private String doings;
    @SerializedName("expireTime")
    private long expireTime;
    @SerializedName("money")
    private int money;
    @SerializedName("following")
    private int following;
    @SerializedName("sharings")
    private String sharings;
    @SerializedName("vipStatus")
    private String vipStatus;
    @SerializedName("username")
    private String username;

    public String getAlbums() {
        return albums;
    }

    public void setAlbums(String albums) {
        this.albums = albums;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getBlogs() {
        return blogs;
    }

    public void setBlogs(String blogs) {
        this.blogs = blogs;
    }

    public String getMiddleUrl() {
        return middleUrl;
    }

    public void setMiddleUrl(String middleUrl) {
        this.middleUrl = middleUrl;
    }

    public String getContribute() {
        return contribute;
    }

    public void setContribute(String contribute) {
        this.contribute = contribute;
    }

    public String getShengwang() {
        return shengwang;
    }

    public void setShengwang(String shengwang) {
        this.shengwang = shengwang;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPosts() {
        return posts;
    }

    public void setPosts(String posts) {
        this.posts = posts;
    }

    public int getRelation() {
        return relation;
    }

    public void setRelation(int relation) {
        this.relation = relation;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getIsteacher() {
        return isteacher;
    }

    public void setIsteacher(String isteacher) {
        this.isteacher = isteacher;
    }

    public String getCredits() {
        return credits;
    }

    public void setCredits(String credits) {
        this.credits = credits;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getFollower() {
        return follower;
    }

    public void setFollower(int follower) {
        this.follower = follower;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAllThumbUp() {
        return allThumbUp;
    }

    public void setAllThumbUp(String allThumbUp) {
        this.allThumbUp = allThumbUp;
    }

    public String getIcoins() {
        return icoins;
    }

    public void setIcoins(String icoins) {
        this.icoins = icoins;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFriends() {
        return friends;
    }

    public void setFriends(String friends) {
        this.friends = friends;
    }

    public String getDoings() {
        return doings;
    }

    public void setDoings(String doings) {
        this.doings = doings;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public String getSharings() {
        return sharings;
    }

    public void setSharings(String sharings) {
        this.sharings = sharings;
    }

    public String getVipStatus() {
        return vipStatus;
    }

    public void setVipStatus(String vipStatus) {
        this.vipStatus = vipStatus;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
