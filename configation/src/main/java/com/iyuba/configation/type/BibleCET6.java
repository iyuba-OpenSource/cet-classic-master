package com.iyuba.configation.type;

/**
 * 六级宝典
 */
public class BibleCET6 implements IAPP {
    @Override
    public boolean isEnglish() {
        return true;
    }

    @Override
    public String TYPE() {
        return "6";
    }

    @Override
    public String APPName() {
        return "英语六级听力";
    }

    @Override
    public String APP() {
        return APPName();
    }

    @Override
    public String PACKAGE_NAME() {
        return "com.iyuba.cet6";
    }

    @Override
    public String AppName() {
        return "Valuablecet6";
    }

    @Override
    public String APPID() {
        return "208";
    }

    @Override
    public String presentId() {
        return "54";
    }

    @Override
    public String book() {
        return "免费赠送六级真题书";
    }

    @Override
    public String courseTypeId() {
        return "4";
    }

    @Override
    public String courseTypeTitle() {
        return "CET6课程";
    }

    @Override
    public String mListen() {
        return "cet6";
    }

    @Override
    public String SMSAPPID() {
        return "38efbfe0bc769";
    }

    @Override
    public String SMSAPPSECRET() {
        return "e043b3e65106670757a30852af6d0a1e";
    }

    @Override
    public String APP_DATA_PATH() {
        return "cet6bible";
    }

    @Override
    public int cetDatabaseLastVersion() {
        return 13;
    }

    @Override
    public int cetDatabaseCurVersion() {
        return 14;
    }

    @Override
    public String BLOG_ACCOUNT_ID() {
        return "242145";
    }

    @Override
    public String ADDAM_APPKEY() {
        return "9c4f2886cda8b379b25b1b67a5400dd9";
    }

    @Override
    public String MOB_APPKEY() {
        return "38efbfe0bc769";
    }

    @Override
    public String MOB_APP_SECRET() {
        return "e043b3e65106670757a30852af6d0a1e";
    }

    @Override
    public String HEAD() {
        return "BDCET6NewScroll";
    }

    @Override
    public int VIP_STATUS() {
        return 4;
    }

    @Override
    public String IMOOC_TYPE_DESC() {
        return "class.cet6";
    }

}
