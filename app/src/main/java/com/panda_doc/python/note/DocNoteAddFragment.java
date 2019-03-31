package com.panda_doc.python.note;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.panda_doc.python.R;
import com.panda_doc.python.conf.Conf;
import com.panda_doc.python.util.NumberUtil;
import com.panda_doc.python.view_model.UserInfoViewModel;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

public class DocNoteAddFragment extends Fragment {

    /**
     * 笔记内容
     */
    EditText editText;
    /**
     * 标记是否正在发送消息
     */
    boolean isSending;

    UserInfoViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isSending = false;

        View root = inflater.inflate(R.layout.fragment_doc_note_add, container, false);

        editText = root.findViewById(R.id.textView2);

        BottomNavigationView navigation = (BottomNavigationView) root.findViewById(R.id.note_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        viewModel = ViewModelProviders.of(this.getActivity()).get(UserInfoViewModel.class);

        return root;
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.doc_note_add_back:
                    /** 返回*/
                    NavHostFragment.findNavController(DocNoteAddFragment.this).navigateUp();
                    return true;
                case R.id.doc_note_add_send:
                    /** 记笔记*/
                    String note = editText.getText().toString();
                    if (note.length() == 0) {
                        /** 内容为空*/
                        Toast.makeText(DocNoteAddFragment.this.getContext(), getString(R.string.note_input_text), Toast.LENGTH_LONG).show();
                    } else if (note.length() < Conf.NOTE_MAX_LENGTH) {
                        /** 上传笔记*/
                        sendNote(note);
                    } else {
                        /** 内容过长*/
                        String showText = getString(R.string.note_text_too_long) + Conf.NOTE_MAX_LENGTH;
                        Toast.makeText(DocNoteAddFragment.this.getContext(), showText, Toast.LENGTH_LONG).show();
                    }
                    return true;
            }
            return false;
        }
    };

    /**
     * 上传笔记
     */
    private void sendNote(final String note) {
        /** 检测当前文章id是否可以加笔记*/
        boolean isInt = NumberUtil.isInteger(viewModel.getCurrentPageNum().get());
        if (!isInt) {
            /** 当前文章编号不是整型,比如index,返回*/
            Log.e(Conf.DOMAIN, " getNoteByDocId isInt false");
            return;
        }

        if (isSending) {
            return;
        }

        if (viewModel.getUserId() == null) {
            Toast.makeText(DocNoteAddFragment.this.getContext(), "请先登录", Toast.LENGTH_LONG).show();
            return;
        }

        isSending = true;

        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Conf.URL_NOTE_ADD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        /** 重置标记*/
                        isSending = false;
                        Log.i(Conf.DOMAIN, " sendNote onResponse " + response);
                        Toast.makeText(DocNoteAddFragment.this.getContext(), getString(R.string.note_send_success), Toast.LENGTH_LONG).show();
                        /** 返回上一级导航*/
                        NavHostFragment.findNavController(DocNoteAddFragment.this).navigateUp();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isSending = false;
                Toast.makeText(DocNoteAddFragment.this.getContext(), getString(R.string.note_check_net_connect), Toast.LENGTH_LONG).show();
                Log.e(Conf.DOMAIN, error.fillInStackTrace().toString());
            }

        }) {
            /**
             * 加入post参数
             */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // 请求参数
                Map<String, String> map = new HashMap<>();
                //new 一个Map  参数放到Map中
                map.put("num", viewModel.getCurrentPageNum().get());
                map.put("note", note);
                map.put("openid", viewModel.getOpenId());
                map.put("name", viewModel.getNickname().get());
                map.put("userid", viewModel.getUserId());
                return map;
            }
        };

        queue.add(stringRequest);
    }

}
