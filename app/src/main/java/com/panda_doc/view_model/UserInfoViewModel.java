package com.panda_doc.view_model;

import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

public class UserInfoViewModel extends ViewModel {

    private String openId;
    private String unionId;
    private ObservableField<String> userId = new ObservableField<>();
    private final ObservableField<String> nickname = new ObservableField<>();
    private final ObservableField<String> headimgurl = new ObservableField<>();

    public ObservableField<String> getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname.set(nickname);
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl.set(headimgurl);
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public ObservableField<String> getHeadimgurl() {
        return headimgurl;
    }

    public ObservableField<String> getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId.set(userId);
    }
}
