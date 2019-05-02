package com.panda_doc.python.view_model;

import android.graphics.Bitmap;

import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

public class UserInfoViewModel extends ViewModel {

    private String openId;
    private String accessToken;
    private String userId;

    private int[] expArr;

    private final ObservableField<String> nickname = new ObservableField<>();
    private final ObservableField<String> sex = new ObservableField<>();
    private final ObservableField<Integer> exp = new ObservableField<>();
    private final ObservableField<String> province = new ObservableField<>();
    private final ObservableField<String> city = new ObservableField<>();
    private final ObservableField<String> headimgurl = new ObservableField<>();
    private final ObservableField<Bitmap> headBitmap = new ObservableField<>();

    //    private final ObservableArrayList<String> titles = new ObservableArrayList<>();
    private final ObservableField<String> currentPageNum = new ObservableField<>();
    private final ObservableField<String> anchor = new ObservableField<>();

//    private ObservableField<String> languageState = new ObservableField();

    /**
     * 用户登录方式
     */
    public static final int LOGIN_WECHAT = 1;
    public static final int LOGIN_QQ = 2;
    private int loginType = 0;

    public ObservableField<String> getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname.set(nickname);
    }

    public void setSex(String sex) {
        this.sex.set(sex);
    }

    public void setProvince(String province) {
        this.province.set(province);
    }

    public void setCity(String city) {
        this.city.set(city);
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl.set(headimgurl);
    }

    public void setHeadBitmap(Bitmap bitmap) {
        this.headBitmap.set(bitmap);
    }

    public Bitmap getHeadBitmap() {
        return headBitmap.get();
    }

    public ObservableField<Bitmap> getHeadBitmapObserver() {
        return headBitmap;
    }

//    public ObservableArrayList<String> getTitles() {
//        return titles;
//    }
//
//    public void updateTitles(ArrayList<String> strings) {
//        this.titles.clear();
//        this.titles.addAll(strings);
//    }

//    public ObservableField<String> getLanguageState() {
//        return languageState;
//    }

//    public void setLanguageState(String languageState) {
//        this.languageState.set(languageState);
//    }

    public ObservableField<String> getCurrentPageNum() {
        return currentPageNum;
    }

    public void setCurrentPageNum(String pageNum) {
        currentPageNum.set(pageNum);
    }

    public ObservableField<String> getAnchor() {
        return anchor;
    }

    public void setAnchor(String anchor) {
        this.anchor.set(anchor);
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public ObservableField<String> getSex() {
        return sex;
    }

    public ObservableField<String> getProvince() {
        return province;
    }

    public ObservableField<String> getCity() {
        return city;
    }

    public ObservableField<String> getHeadimgurl() {
        return headimgurl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int[] getExpArr() {
        return expArr;
    }

    public void setExpArr(int[] expArr) {
        this.expArr = expArr;
    }

    public ObservableField<Integer> getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp.set(exp);
    }

    public int getLoginType() {
        return loginType;
    }

    public void setLoginType(int loginType) {
        this.loginType = loginType;
    }
}
