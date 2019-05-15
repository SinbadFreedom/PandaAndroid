package com.panda_doc.python.view_model;

import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

public class UserInfoViewModel extends ViewModel {

    private String openId;
    private ObservableField<String> userId = new ObservableField<>();
    private final ObservableField<String> nickname = new ObservableField<>();
    private final ObservableField<String> headimgurl = new ObservableField<>();

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

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl.set(headimgurl);
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
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

    public int getLoginType() {
        return loginType;
    }

    public void setLoginType(int loginType) {
        this.loginType = loginType;
    }

}
