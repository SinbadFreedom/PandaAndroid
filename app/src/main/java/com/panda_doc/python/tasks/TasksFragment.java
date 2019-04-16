/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.panda_doc.python.tasks;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.panda_doc.python.R;
import com.panda_doc.python.conf.Conf;
import com.panda_doc.python.note.Title;
import com.panda_doc.python.uikit.NetworkUtil;
import com.panda_doc.python.util.NumberUtil;
import com.panda_doc.python.view_model.UserInfoViewModel;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.GravityCompat;
import androidx.databinding.Observable;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

public class TasksFragment extends Fragment {

    private WebView mWebView;
    private PopupMenu popupMenu;

    private TextView viewNickName;
    private ImageView viewHeadImage;
    private TextView textViewLevel;
    private TextView textViewExp;
    private ProgressBar progressBar;

    private DrawerLayout mDrawerLayout;
    private TaskAdapter taskAdapter;

    private UserInfoViewModel userInfoViewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        userInfoViewModel = ViewModelProviders.of(this.getActivity()).get(UserInfoViewModel.class);

        View root = inflater.inflate(R.layout.tasks_act, container, false);

        mDrawerLayout = (DrawerLayout) root.findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        taskAdapter = new TaskAdapter(this.getContext());

        ListView listView = (ListView) root.findViewById(R.id.tasks_list);
        listView.setAdapter(taskAdapter);
        /** 点击标题事件*/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view instanceof LinearLayout) {
                    TextView textView = (TextView) ((LinearLayout) view).getChildAt(0);
                    String str = (String) textView.getText();
                    str = str.trim();
                    String[] numArr = str.split("\\.");
                    if (numArr.length > 0) {
                        /** 获取文章编号*/
                        String num = numArr[0];
                        /** 获取完整标题*/
                        Title title = taskAdapter.getItem(position);
                        /**
                         * 获取标题id
                         * 与showdown转化的html标题id规则统一，目前是去掉了空格和“.”， 变小写
                         * 有可能有特殊字符过滤，后续添加。
                         */
                        String anchor = title.getFullTitle().trim().split(" " + "")[0];
                        /** 切换文章内容*/
                        showWebPage(num, anchor);
                    } else {
                        Log.e(Conf.DOMAIN, " numArr.length == 0 " + str);
                    }
                } else {
                    Log.e(Conf.DOMAIN, "view instanceof LinearLayout false position " + position);
                }
                closeTaskDrawer();
            }
        });

        mWebView = root.findViewById(R.id.task_web_view);
        /** 统一初始化WebView设置*/
        NetworkUtil.initWebView(mWebView);
        /** 修复加载外部css和js不生效的问题 重置当前页面的信息和锚点信息*/
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (!url.contains(Conf.URL_DOC_CONTENT_PRE)) {
                    /** 非api文章返回，置空 currentPageNum*/
                    userInfoViewModel.setCurrentPageNum(null);
                    userInfoViewModel.setAnchor(null);
                }
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) root.findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        /** 语言选择弹窗*/
        popupMenu = new PopupMenu(this.getContext(), root.findViewById(R.id.navigation_translate));
        popupMenu.getMenuInflater().inflate(R.menu.language, popupMenu.getMenu());
        initPopMenuEvent();

        /** 名字，头像，经验，等级*/
        viewNickName = root.findViewById(R.id.user_name);
        viewHeadImage = root.findViewById(R.id.img_header_icon);
        textViewLevel = (TextView) root.findViewById(R.id.info_level);
        textViewExp = (TextView) root.findViewById(R.id.info_exp);
        progressBar = (ProgressBar) root.findViewById(R.id.progressBar);

        if (null != userInfoViewModel.getNickname().get()) {
            /** 导航切换回来，初始化 设置名称*/
            viewNickName.setText(userInfoViewModel.getNickname().get());
        } else {
            /** 首次开启应用 注册 名字和头像变化事件 */
            userInfoViewModel.getNickname().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    String nameStr = ((ObservableField<String>) sender).get();
                    viewNickName.setText(nameStr);
                    Log.i(Conf.DOMAIN, " " + propertyId);
                }
            });
        }

        if (null != userInfoViewModel.getHeadBitmap()) {
            /** 导航切换回来，初始化 设置头像*/
            viewHeadImage.setImageBitmap(userInfoViewModel.getHeadBitmap());
        } else {
            userInfoViewModel.getHeadBitmapObserver().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    Bitmap bitmap = ((ObservableField<Bitmap>) sender).get();
                    viewHeadImage.setImageBitmap(bitmap);
                }
            });
        }

        if (null != userInfoViewModel.getExp().get()) {
            /** 导航切换回来，初始化 设置等级和经验*/
            int exp = userInfoViewModel.getExp().get();
            int levelIndex = NumberUtil.getLevelByExp(exp, userInfoViewModel.getExpArr());
            int expMax = NumberUtil.getTotalExpByLevel(levelIndex, userInfoViewModel.getExpArr());
            /** 等级*/
            String levelTxt = (levelIndex + 1) + "";
            textViewLevel.setText(levelTxt);
            /** 经验*/
            textViewExp.setText(exp + "/" + expMax);
            /** 经验条*/
            progressBar.setProgress(exp);
            progressBar.setMax(expMax);
        } else {
            userInfoViewModel.getExp().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    Integer exp = ((ObservableField<Integer>) sender).get();
                    int levelIndex = NumberUtil.getLevelByExp(exp, userInfoViewModel.getExpArr());
                    int expMax = NumberUtil.getTotalExpByLevel(levelIndex, userInfoViewModel.getExpArr());
                    /** 更新等级*/
                    String levelTxt = (levelIndex + 1) + "";
                    textViewLevel.setText(levelTxt);
                    /** 更新经验*/
                    textViewExp.setText(exp + "/" + expMax);
                    /** 经验条*/
                    progressBar.setProgress(exp);
                    progressBar.setMax(expMax);
                }
            });
        }

        /** 语言状态*/
        if (userInfoViewModel.getLanguageState().get() == null) {
            userInfoViewModel.setLanguageState(UserInfoViewModel.LAN_ZH_CN);
            /** 切换语言*/
            userInfoViewModel.getLanguageState().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    /** 当前页面数据更新*/
                    showWebPage(userInfoViewModel.getCurrentPageNum().get(), userInfoViewModel.getAnchor().get());
                    /** 目录数据更新*/
                    getCatalog();
                }
            });
        }

        /** 目录数据*/
        if (userInfoViewModel.getTitles().size() != 0) {
            taskAdapter.initContents(userInfoViewModel.getTitles());
        } else {
            /** 目录数据初始化*/
            getCatalog();

            userInfoViewModel.getTitles().addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<String>>() {

                @Override
                public void onChanged(ObservableList<String> sender) {

                }

                @Override
                public void onItemRangeChanged(ObservableList<String> sender, int positionStart, int itemCount) {

                }

                @Override
                public void onItemRangeInserted(ObservableList<String> sender, int positionStart, int itemCount) {
                    /** 初始化目录数据*/
                    taskAdapter.initContents(sender);
                }

                @Override
                public void onItemRangeMoved(ObservableList<String> sender, int fromPosition, int toPosition, int itemCount) {

                }

                @Override
                public void onItemRangeRemoved(ObservableList<String> sender, int positionStart, int itemCount) {

                }
            });
        }

        /** currentPageNum 为空 显示Index主页, 不为空 显示当前页， 用在从笔记页面切换回来的时候*/
        if (userInfoViewModel.getCurrentPageNum().get() == null) {
            this.showWebPage(Conf.URL_INDEX, null);
        } else {
            this.showWebPage(userInfoViewModel.getCurrentPageNum().get(), userInfoViewModel.getAnchor().get());
        }

        return root;
    }

    private void showWebPage(String pageNum, String anc) {
        if (null == pageNum) {
            Log.e(Conf.DOMAIN, "showWebPage pageNum == null");
            return;
        }

        userInfoViewModel.setCurrentPageNum(pageNum);
        userInfoViewModel.setAnchor(anc);

        String url = Conf.URL_DOC_CONTENT_PRE + userInfoViewModel.getLanguageState().get() + "/" + pageNum + ".html";

        if (anc != null) {
            /** 跳转锚点*/
            url = url + "#" + anc;
        }
        mWebView.loadUrl(url);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_about:
                    /** 关于*/
                    NavHostFragment.findNavController(TasksFragment.this).navigate(R.id.action_tasksFragment3_to_about);
                    return true;
                case R.id.navigation_note:
                    /** 笔记*/
                    boolean isInt = NumberUtil.isInteger(userInfoViewModel.getCurrentPageNum().get());
                    if (isInt) {
                        NavHostFragment.findNavController(TasksFragment.this).navigate(R.id.action_tasksFragment3_to_doc_note2);
                    } else {
                        /** 当前文章编号不是整型,比如index,返回*/
                        Toast.makeText(TasksFragment.this.getContext(), getString(R.string.can_not_add_note), Toast.LENGTH_LONG).show();
                    }
                    return true;
                case R.id.navigation_translate:
                    /** 切换语言状态*/
                    popupMenu.show();
                    return true;
                case R.id.navigation_ranking_list:
                    /** 排行榜*/
                    NavHostFragment.findNavController(TasksFragment.this).navigate(R.id.action_tasksFragment3_to_rankListFragment);
                    break;
            }
            return false;
        }
    };

    private void initPopMenuEvent() {
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                /** 控件每一个item的点击事件*/
                switch (item.getItemId()) {
                    case R.id.popupmenu_ch_cn:
                        userInfoViewModel.setLanguageState(UserInfoViewModel.LAN_ZH_CN);
                        break;
                    case R.id.popupmenu_en:
                        userInfoViewModel.setLanguageState(UserInfoViewModel.LAN_EN);
                        break;
                }
                return true;
            }
        });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                /** 控件消失时的事件*/
            }
        });
    }

    /**
     * 按返回键时如果开启了滑动屏，则关闭
     */
    public void closeTaskDrawer() {
        if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        }
    }

    /**
     * 初始化目录内容
     */
    private void getCatalog() {
        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        String catalogUrl = Conf.URL_DOC_CONTENT_PRE + userInfoViewModel.getLanguageState().get() + "/" + Conf.URL_CATALOG;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, catalogUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("")) {
                            return;
                        }
                        String[] titles = response.split("\n");
                        ArrayList<String> titleList = new ArrayList<>();
                        Collections.addAll(titleList, titles);
                        /** 更新ViewModel 目录数据*/
                        userInfoViewModel.updateTitles(titleList);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(Conf.DOMAIN, error.fillInStackTrace().toString());
            }
        }) {

            /** 解决 Volley utf-8 乱码*/
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String parsed;
                try {
                    parsed = new String(response.data, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    // Since minSdkVersion = 8, we can't call
                    // new String(response.data, Charset.defaultCharset())
                    // So suppress the warning instead.
                    parsed = new String(response.data);
                }
                return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        queue.add(stringRequest);
    }

}
