package com.panda_doc.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.panda_doc.DocActivity;
import com.panda_doc.conf.Constants;
import com.panda_doc.wechat.NetworkUtil;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelbiz.SubscribeMessage;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.modelbiz.WXOpenBusinessView;
import com.tencent.mm.opensdk.modelbiz.WXOpenBusinessWebview;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private static String TAG = "MicroMsg.WXEntryActivity";

    private IWXAPI api;
    private MyHandler handler;
    /**
     * token
     */
    private String refreshToken;
    private String openId;
    private String accessToken;
    private String scope;
    /**
     * user info
     */
    private String unionid;
    private String nickname;
    private String sex;
    private String province;
    private String city;
    private String headimgurl;

    private class MyHandler extends Handler {
          private final WeakReference<WXEntryActivity> wxEntryActivityWeakReference;

        public MyHandler(WXEntryActivity wxEntryActivity) {
            wxEntryActivityWeakReference = new WeakReference<WXEntryActivity>(wxEntryActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            int tag = msg.what;
            JSONObject json;
            Bundle data = msg.getData();
            switch (tag) {
                case NetworkUtil.GET_TOKEN: {
                    try {
                        json = new JSONObject(data.getString("result"));
                        openId = json.getString(Constants.KEY_OPENID);
                        accessToken = json.getString(Constants.KEY_ACCESS_TOKEN);
                        refreshToken = json.getString(Constants.KEY_REFRESH_TOKEN);
                        scope = json.getString(Constants.KEY_SCOPE);

                        if (accessToken != null && openId != null) {
                            NetworkUtil.sendWxAPI(this, String.format("https://api.weixin.qq.com/sns/auth?" +
                                    "access_token=%s&openid=%s", accessToken, openId), NetworkUtil.CHECK_TOKEN);
                        } else {
                            Toast.makeText(WXEntryActivity.this, "请先获取code", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    break;
                }
                case NetworkUtil.CHECK_TOKEN: {
                    try {
                        json = new JSONObject(data.getString("result"));
                        int errcode = json.getInt("errcode");
                        if (errcode == 0) {
                            NetworkUtil.sendWxAPI(handler, String.format("https://api.weixin.qq.com/sns/userinfo?" +
                                    "access_token=%s&openid=%s", accessToken, openId), NetworkUtil.GET_INFO);
                        } else {
                            NetworkUtil.sendWxAPI(handler, String.format("https://api.weixin.qq.com/sns/oauth2/refresh_token?" +
                                            "appid=%s&grant_type=refresh_token&refresh_token=%s", Constants.WX_APP_ID, refreshToken),
                                    NetworkUtil.REFRESH_TOKEN);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    break;
                }
                case NetworkUtil.REFRESH_TOKEN: {
                    try {
                        json = new JSONObject(data.getString("result"));
                        openId = json.getString(Constants.KEY_OPENID);
                        accessToken = json.getString(Constants.KEY_ACCESS_TOKEN);
                        refreshToken = json.getString(Constants.KEY_REFRESH_TOKEN);
                        scope = json.getString(Constants.KEY_SCOPE);
                        NetworkUtil.sendWxAPI(handler, String.format("https://api.weixin.qq.com/sns/userinfo?" +
                                "access_token=%s&openid=%s", accessToken, openId), NetworkUtil.GET_INFO);
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    break;
                }
                case NetworkUtil.GET_INFO: {
                    try {
                        json = new JSONObject(data.getString("result"));
                        unionid = json.getString(Constants.KEY_UNIONID);
                        headimgurl = json.getString(Constants.KEY_HEADIMGURL);
                        String encode = getcode(json.getString(Constants.KEY_NICKNAME));
                        nickname = new String(json.getString(Constants.KEY_NICKNAME).getBytes(encode), "utf-8");
                        sex = json.getString(Constants.KEY_SEX);
                        province = json.getString(Constants.KEY_PROVINCE);
                        city = json.getString(Constants.KEY_CITY);

                        updateWXLoginState();
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    break;
                }
            }
        }

        private void updateWXLoginState() {
            /** 传递用户数据到Doc应用*/
            Intent intent = new Intent(wxEntryActivityWeakReference.get(), DocActivity.class);
            intent.putExtra(Constants.KEY_OPENID, openId);
            intent.putExtra(Constants.KEY_ACCESS_TOKEN, accessToken);
            intent.putExtra(Constants.KEY_REFRESH_TOKEN, refreshToken);
            intent.putExtra(Constants.KEY_SCOPE, scope);

            intent.putExtra(Constants.KEY_UNIONID, unionid);
            intent.putExtra(Constants.KEY_HEADIMGURL, headimgurl);
            intent.putExtra(Constants.KEY_NICKNAME, nickname);
            intent.putExtra(Constants.KEY_SEX, sex);
            intent.putExtra(Constants.KEY_PROVINCE, province);
            intent.putExtra(Constants.KEY_CITY, city);
            wxEntryActivityWeakReference.get().startActivity(intent);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID, false);
        handler = new MyHandler(this);

        try {
            Intent intent = getIntent();
            api.handleIntent(intent, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
        finish();
    }

    @Override
    public void onResp(BaseResp resp) {
        int result = resp.errCode;
        Log.i(TAG, "onResp " + result);

        if (resp.getType() == ConstantsAPI.COMMAND_SUBSCRIBE_MESSAGE) {
            SubscribeMessage.Resp subscribeMsgResp = (SubscribeMessage.Resp) resp;
            String text = String.format("openid=%s\ntemplate_id=%s\nscene=%d\naction=%s\nreserved=%s",
                    subscribeMsgResp.openId, subscribeMsgResp.templateID, subscribeMsgResp.scene, subscribeMsgResp.action, subscribeMsgResp.reserved);

            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
        }

        if (resp.getType() == ConstantsAPI.COMMAND_LAUNCH_WX_MINIPROGRAM) {
            WXLaunchMiniProgram.Resp launchMiniProgramResp = (WXLaunchMiniProgram.Resp) resp;
            String text = String.format("openid=%s\nextMsg=%s\nerrStr=%s",
                    launchMiniProgramResp.openId, launchMiniProgramResp.extMsg, launchMiniProgramResp.errStr);

            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
        }

        if (resp.getType() == ConstantsAPI.COMMAND_OPEN_BUSINESS_VIEW) {
            WXOpenBusinessView.Resp launchMiniProgramResp = (WXOpenBusinessView.Resp) resp;
            String text = String.format("openid=%s\nextMsg=%s\nerrStr=%s\nbusinessType=%s",
                    launchMiniProgramResp.openId, launchMiniProgramResp.extMsg, launchMiniProgramResp.errStr, launchMiniProgramResp.businessType);

            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
        }

        if (resp.getType() == ConstantsAPI.COMMAND_OPEN_BUSINESS_WEBVIEW) {
            WXOpenBusinessWebview.Resp response = (WXOpenBusinessWebview.Resp) resp;
            String text = String.format("businessType=%d\nresultInfo=%s\nret=%d", response.businessType, response.resultInfo, response.errCode);

            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
        }

        if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
            SendAuth.Resp authResp = (SendAuth.Resp) resp;
            final String code = authResp.code;
            NetworkUtil.sendWxAPI(handler, String.format("https://api.weixin.qq.com/sns/oauth2/access_token?" +
                            "appid=%s&secret=%s&code=%s&grant_type=authorization_code", Constants.WX_APP_ID,
                    Constants.WX_APP_SECRET, code), NetworkUtil.GET_TOKEN);
        }
        finish();
    }

    private String getcode(String str) {
        String[] encodelist = {"GB2312", "ISO-8859-1", "UTF-8", "GBK", "Big5", "UTF-16LE", "Shift_JIS", "EUC-JP"};
        for (String anEncodelist : encodelist) {
            try {
                if (str.equals(new String(str.getBytes(anEncodelist), anEncodelist))) {
                    return anEncodelist;
                }
            } catch (Exception ignored) {

            }
        }
        return "";
    }
}