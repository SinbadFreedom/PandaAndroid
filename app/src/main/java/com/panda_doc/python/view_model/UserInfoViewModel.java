package com.panda_doc.python.view_model;

import android.graphics.Bitmap;

import java.util.ArrayList;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.ViewModel;

public class UserInfoViewModel extends ViewModel {

    private final ObservableField<String> nickname = new ObservableField<>();
    private final ObservableField<String> sex = new ObservableField<>();
    private final ObservableField<String> province = new ObservableField<>();
    private final ObservableField<String> city = new ObservableField<>();
    private final ObservableField<String> country = new ObservableField<>();
    private final ObservableField<String> headimgurl = new ObservableField<>();
    private final ObservableField<Bitmap> headBitmap = new ObservableField<>();

    private final ObservableArrayList<String> titles = new ObservableArrayList<>();
    private final ObservableField<String> currentPageNum = new ObservableField<>();
    private final ObservableField<String> anchor = new ObservableField<>();

    /**
     * 语言状态
     */
    public static final int LAN_ZH_CN = 1;
    public static final int LAN_EN = 2;
    private ObservableInt languageState = new ObservableInt();

    public String getNickname() {
        return nickname.get();
    }

    public ObservableField<String> getNicknameObserver() {
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

    public void setCountry(String country) {
        this.country.set(country);
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

    public ObservableArrayList<String> getTitles() {
        return titles;
    }

    public void updateTitles(ArrayList<String> strings) {
        this.titles.clear();
        this.titles.addAll(strings);
    }

    public ObservableInt getLanguageState() {
        return languageState;
    }

    public void setLanguageState(int languageState) {
        this.languageState.set(languageState);
    }

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
}
