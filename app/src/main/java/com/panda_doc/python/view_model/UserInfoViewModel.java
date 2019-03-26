package com.panda_doc.python.view_model;

import android.graphics.Bitmap;

import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

public class UserInfoViewModel extends ViewModel {

    private final ObservableField<String> nickname = new ObservableField<>();
    private final ObservableField<String> sex = new ObservableField<>();
    private final ObservableField<String> province = new ObservableField<>();
    private final ObservableField<String> city = new ObservableField<>();
    private final ObservableField<String> country = new ObservableField<>();
    private final ObservableField<String> headimgurl = new ObservableField<>();
    private final ObservableField<Bitmap> headBitmap = new ObservableField<>();

    public String getNickname() {
        return nickname.get();
    }

    public ObservableField<String> getNicknameObserver() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname.set(nickname);
    }

    public ObservableField<String> getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex.set(sex);
    }

    public ObservableField<String> getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province.set(province);
    }

    public ObservableField<String> getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city.set(city);
    }

    public ObservableField<String> getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country.set(country);
    }

    public String getHeadimgurl() {
        return headimgurl.get();
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
}
