package com.panda_doc.python.rank_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.panda_doc.python.R;
import com.panda_doc.python.conf.Conf;
import com.panda_doc.python.conf.Constants;
import com.panda_doc.python.view_model.UserInfoViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

public class RankListFragment extends Fragment {

    WebView webView;
    UserInfoViewModel userInfoViewModel;
    PopupMenu rankMenuDay;
    PopupMenu rankMenuWeek;
    PopupMenu rankMenuMonth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        userInfoViewModel = ViewModelProviders.of(this.getActivity()).get(UserInfoViewModel.class);

        View root = inflater.inflate(R.layout.fragment_rank_list, container, false);
        /** 排行榜*/
        webView = (WebView) root.findViewById(R.id.rank_list);

        BottomNavigationView navigation = (BottomNavigationView) root.findViewById(R.id.rank_list_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        String openId = userInfoViewModel.getOpenId();
        String userId = userInfoViewModel.getUserId();
        String url = Conf.URL_RANK_LIST + "?openid=" + openId + "&userid=" + userId + "&type=" + 1;
        webView.loadUrl(url);

        /** 日榜弹出窗口*/
        rankMenuDay = new PopupMenu(this.getContext(), root.findViewById(R.id.rank_day));
        rankMenuDay.getMenuInflater().inflate(R.menu.menu_rank_day, rankMenuDay.getMenu());
        initPopMenuEventRankDay();

        /** 周榜弹出窗口*/
        rankMenuWeek = new PopupMenu(this.getContext(), root.findViewById(R.id.rank_week));
        rankMenuWeek.getMenuInflater().inflate(R.menu.menu_rank_week, rankMenuWeek.getMenu());
        initPopMenuEventRankWeek();

        /** 月榜弹出窗口*/
        rankMenuMonth = new PopupMenu(this.getContext(), root.findViewById(R.id.rank_month));
        rankMenuMonth.getMenuInflater().inflate(R.menu.menu_rank_month, rankMenuMonth.getMenu());
        initPopMenuEventRankMonth();

        return root;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.rank_list_back:
                    NavHostFragment.findNavController(RankListFragment.this).navigateUp();
                    return true;
                case R.id.rank_day:
                    /** 日排行榜*/
                    rankMenuDay.show();
                    break;
                case R.id.rank_week:
                    /** 周排行榜*/
                    rankMenuWeek.show();
                    break;
                case R.id.rank_month:
                    /** 月排行榜*/
                    rankMenuMonth.show();
                    break;
                case R.id.rank_all:
                    /** 总排行榜*/
                    String url = Conf.URL_RANK_LIST + "?type=" + Constants.TYPE_ALL;
                    webView.loadUrl(url);
                    break;
            }
            return false;
        }
    };

    private void initPopMenuEventRankDay() {
        rankMenuDay.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                /** 控件每一个item的点击事件*/
                switch (item.getItemId()) {
                    case R.id.rank_today:
                        String url = Conf.URL_RANK_LIST + "?type=" + Constants.TYPE_TODAY;
                        webView.loadUrl(url);
                        break;
                    case R.id.rank_yesterday:
                        url = Conf.URL_RANK_LIST + "?type=" + Constants.TYPE_YESTERDAY;
                        webView.loadUrl(url);
                        break;
                }
                return true;
            }
        });
    }

    private void initPopMenuEventRankWeek() {
        rankMenuWeek.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                /** 控件每一个item的点击事件*/
                switch (item.getItemId()) {
                    case R.id.rank_this_week:
                        String url = Conf.URL_RANK_LIST + "?type=" + Constants.TYPE_WEEK;
                        webView.loadUrl(url);
                        break;
                    case R.id.rank_last_week:
                        url = Conf.URL_RANK_LIST + "?type=" + Constants.TYPE_WEEK_LAST;
                        webView.loadUrl(url);
                        break;
                }
                return true;
            }
        });
    }

    private void initPopMenuEventRankMonth() {
        rankMenuMonth.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                /** 控件每一个item的点击事件*/
                switch (item.getItemId()) {
                    case R.id.rank_this_month:
                        String url = Conf.URL_RANK_LIST + "?type=" + Constants.TYPE_MONTH;
                        webView.loadUrl(url);
                        break;
                    case R.id.rank_last_month:
                        url = Conf.URL_RANK_LIST + "?type=" + Constants.TYPE_MONTH_LAST;
                        webView.loadUrl(url);
                        break;
                }
                return true;
            }
        });
    }
}
