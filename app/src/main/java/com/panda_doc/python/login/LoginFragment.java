package com.panda_doc.python.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.panda_doc.python.R;
import com.panda_doc.python.conf.Constants;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment {

    ImageButton wechatLogin;
    private IWXAPI api;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.login, container, false);

        regToWx();
        wechatLogin = (ImageButton) root.findViewById(R.id.login_wechat);
        wechatLogin.setImageResource(R.drawable.icon48_wx_button);
        wechatLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** 微信登陆*/
                getWxUserInfo();
            }
        });

        return root;
    }

    /**
     * app注册到微信
     */
    private void regToWx() {
        /** 通过WXAPIFactory工厂，获取IWXAPI的实例*/
        api = WXAPIFactory.createWXAPI(this.getContext(), Constants.WX_APP_ID, false);
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
