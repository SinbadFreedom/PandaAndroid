package com.panda_doc.python.wechat;

import android.content.Context;

import com.panda_doc.python.conf.Constants;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WeChatSdk {

    private IWXAPI api;

    public WeChatSdk(Context context) {
        /** 微信登陆*/
        api = WXAPIFactory.createWXAPI(context, Constants.WX_APP_ID, false);
    }

    public void loginWeChat() {
        /** 经过测试屏蔽这个regToWx方法，没有影响，能够正确获取用户信息，暂时先保留该方法*/
        regToWx();
        getWxUserInfo();
    }

    /**
     * app注册到微信
     */
    private void regToWx() {
        /** 通过WXAPIFactory工厂，获取IWXAPI的实例*/
        /** 将应用的appId注册到微信*/
        api.registerApp(Constants.WX_APP_ID);
    }

    /**
     * 获取用户信息
     */
    private void getWxUserInfo() {
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "python_doc_android_state";
        api.sendReq(req);
    }
}
