package com.panda_doc.python.qq;

import android.app.Activity;
import android.content.Intent;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.panda_doc.python.DocActivity;
import com.panda_doc.python.conf.Conf;
import com.panda_doc.python.conf.Constants;
import com.panda_doc.python.view_model.UserInfoViewModel;
import com.tencent.connect.UnionInfo;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

public class QQSDKListener implements IUiListener {

    private Activity activity;
    private Tencent mTencent;
    private UserInfo mInfo;
    private boolean isServerSideLogin = false;
    /**
     * token
     */
    private String openId;
    private String accessToken;
    private String expires;
    private String expires_time;
    private String pf;
    /**
     * user info
     */
    private String nickname;
    private String sex;
    private String province;
    private String city;
    private String headimgurl;
    private String unionid;

    @Override
    public void onComplete(Object response) {
        if (null == response) {
            Log.e(Conf.DOC_TAG, "返回为空 登录失败 null == response");
            return;
        }
        JSONObject jsonResponse = (JSONObject) response;
        if (null != jsonResponse && jsonResponse.length() == 0) {
            Log.e(Conf.DOC_TAG, "返回为空 登录失败 jsonResponse.length() == 0");
            return;
        }

        Log.d(Conf.DOC_TAG, "AuthorSwitch_SDK:" + SystemClock.elapsedRealtime());

        /** 解析登陆信息*/
        try {
            /** 初始化 access_token openid*/
            accessToken = jsonResponse.getString(com.tencent.connect.common.Constants.PARAM_ACCESS_TOKEN);
            openId = jsonResponse.getString(com.tencent.connect.common.Constants.PARAM_OPEN_ID);
            expires = jsonResponse.getString(com.tencent.connect.common.Constants.PARAM_EXPIRES_IN);
            expires_time = jsonResponse.getString(com.tencent.connect.common.Constants.PARAM_EXPIRES_TIME);
            pf = jsonResponse.getString(com.tencent.connect.common.Constants.PARAM_PLATFORM_ID);
            mTencent.setAccessToken(accessToken, expires);
            mTencent.setOpenId(openId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /** 获取用户信息*/
        getUserInfo();
    }

    @Override
    public void onError(UiError uiError) {

    }

    @Override
    public void onCancel() {

    }

    public QQSDKListener(Activity activity) {
        this.activity = activity;
        mTencent = Tencent.createInstance(Constants.APP_ID_QQ, activity.getApplicationContext());
    }

    public void loginQQ() {
        if (!mTencent.isSessionValid()) {
            mTencent.login(activity, "all", this);
            isServerSideLogin = false;
            Log.d(Conf.DOC_TAG, "FirstLaunch_SDK:" + SystemClock.elapsedRealtime());
        } else {
            if (isServerSideLogin) { // Server-Side 模式的登陆, 先退出，再进行SSO登陆
                mTencent.logout(activity);
                mTencent.login(activity, "all", this);
                isServerSideLogin = false;
                Log.d(Conf.DOC_TAG, "FirstLaunch_SDK:" + SystemClock.elapsedRealtime());
                return;
            }
            mTencent.logout(activity);
            getUserInfo();
        }
    }

    private void getUserInfo() {
        if (mTencent == null || !mTencent.isSessionValid()) {
            Log.e(Conf.DOC_TAG, " getUserInfo mTencent == null || !mTencent.isSessionValid()");
            return;
        }

        IUiListener listener = new IUiListener() {

            @Override
            public void onError(UiError e) {

            }

            @Override
            public void onComplete(final Object response) {
                /** 用户文本信息*/
                /**
                 ret
                 msg
                 is_lost
                 nickname
                 gender
                 province
                 city
                 year
                 constellation
                 figureurl
                 figureurl_1
                 figureurl_2
                 figureurl_qq_1
                 figureurl_qq_2
                 figureurl_qq
                 figureurl_type
                 is_yellow_vip
                 vip
                 yellow_vip_level
                 level
                 is_yellow_year_vip
                 * */
                JSONObject jsonResponse = (JSONObject) response;
                try {
                    nickname = jsonResponse.getString("nickname");
                    sex = jsonResponse.getString("gender");
                    province = jsonResponse.getString("province");
                    city = jsonResponse.getString("city");
                    headimgurl = jsonResponse.getString("figureurl");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                /** 获取 unionID*/
                getUnionId();
            }

            @Override
            public void onCancel() {

            }
        };
        mInfo = new UserInfo(activity, mTencent.getQQToken());
        mInfo.getUserInfo(listener);
    }

    private void getUnionId() {
        if (mTencent == null || !mTencent.isSessionValid()) {
            Log.e(Conf.DOC_TAG, " getUnionId mTencent == null || !mTencent.isSessionValid()");
            return;
        }
        IUiListener listener = new IUiListener() {
            @Override
            public void onError(UiError e) {
            }

            @Override
            public void onComplete(final Object response) {
                /**
                 unionid 格式
                 {
                 "client_id":"YOUR_APPID",
                 "openid":"YOUR_OPENID",
                 "unionid":"YOUR_UNIONID"
                 }
                 */
                /** */
                JSONObject jsonResponse = (JSONObject) response;
                try {
                    unionid = jsonResponse.getString("unionid");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                /** 登录成功启动DocApp*/
                startDocApp();
            }

            @Override
            public void onCancel() {
            }
        };
        UnionInfo unionInfo = new UnionInfo(activity, mTencent.getQQToken());
        unionInfo.getUnionId(listener);
    }

    private void startDocApp() {
        /** 传递用户数据到Doc应用*/
        Intent intent = new Intent(activity, DocActivity.class);
        intent.putExtra(Constants.KEY_OPENID, openId);
        intent.putExtra(Constants.KEY_ACCESS_TOKEN, accessToken);
        intent.putExtra(Constants.KEY_UNIONID, unionid);
        intent.putExtra(Constants.KEY_HEADIMGURL, headimgurl);
        intent.putExtra(Constants.KEY_NICKNAME, nickname);
        intent.putExtra(Constants.KEY_SEX, sex);
        intent.putExtra(Constants.KEY_PROVINCE, province);
        intent.putExtra(Constants.KEY_CITY, city);
        activity.startActivity(intent);
    }
}
