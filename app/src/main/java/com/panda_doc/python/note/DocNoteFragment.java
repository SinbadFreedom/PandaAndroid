package com.panda_doc.python.note;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

public class DocNoteFragment extends Fragment {

    NoteAdapter noteAdapter = new NoteAdapter();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_doc_note, container, false);

        /** 笔记列表*/
        ListView listView = (ListView) root.findViewById(R.id.note_list);
        listView.setAdapter(noteAdapter);
        this.getCurrentPageNote();

        BottomNavigationView navigation = (BottomNavigationView) root.findViewById(R.id.note_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        return root;
    }

    /**
     * 获取文章对应的笔记
     */
    private void getCurrentPageNote() {
        UserInfoViewModel viewModel = ViewModelProviders.of(this.getActivity()).get(UserInfoViewModel.class);

        boolean isInt = NumberUtil.isInteger(viewModel.getCurrentPageNum().get());
        if (!isInt) {
            /** 当前文章编号不是整型,比如index,返回*/
            Log.e(Conf.LOG_TAG, " getCurrentPageNote isInt false");
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        String url = Conf.URL_GET_NOTE + viewModel.getCurrentPageNum().get();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("")) {
                            return;
                        }

                        try {
                            JSONArray jsonObject = new JSONArray(response);
                            ArrayList<Note> notes = new ArrayList<>();
                            for (int i = 0; i < jsonObject.length(); i++) {
                                //TODO 笔记内容，头像，昵称
                                JSONObject noteObj = (JSONObject) jsonObject.get(i);
                                String noteText = noteObj.getString(Conf.KEY_NOTE_INFO);
                                Note note = new Note("", "", noteText);
                                notes.add(note);
                            }
                            noteAdapter.setNoteList(notes);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(Conf.LOG_TAG, " response " + response);
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(Conf.LOG_TAG, error.fillInStackTrace().toString());
            }
        });
        queue.add(stringRequest);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.doc_note_back:
                    /** 返回*/
                    NavHostFragment.findNavController(DocNoteFragment.this).navigateUp();
                    return true;
                case R.id.doc_note_add_note:
                    /** 记笔记*/
                    NavHostFragment.findNavController(DocNoteFragment.this).navigate(R.id.action_doc_note_to_docNoteAddFragment3);
                    return true;
            }
            return false;
        }
    };
}
