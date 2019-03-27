package com.panda_doc.python.about;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.panda_doc.python.R;
import com.panda_doc.python.conf.Conf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public class MoreApp extends Fragment {

    private WebView webView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_more_app, container, false);

        /** 排行榜*/
        webView = (WebView) root.findViewById(R.id.web_view_about);

        BottomNavigationView navigation = (BottomNavigationView) root.findViewById(R.id.navigation_more_app);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        webView.loadUrl(Conf.URL_MORE_APP);
        return root;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_more_app:
                    NavHostFragment.findNavController(MoreApp.this).navigateUp();
                    return true;
            }
            return false;
        }
    };
}
