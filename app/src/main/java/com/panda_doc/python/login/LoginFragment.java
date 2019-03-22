package com.panda_doc.python.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.panda_doc.python.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public class LoginFragment extends Fragment {


    ImageButton wechatLogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.login, container, false);

        wechatLogin = (ImageButton) root.findViewById(R.id.login_wechat);

        wechatLogin.setImageResource(R.drawable.icon48_wx_button);

        wechatLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** 微信登陆*/
                NavHostFragment.findNavController(LoginFragment.this).navigate(R.id.action_doc_login_to_tasksFragment3);
            }
        });

        return root;
    }

}
