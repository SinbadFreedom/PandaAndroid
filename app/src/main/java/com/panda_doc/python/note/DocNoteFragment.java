package com.panda_doc.python.note;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.panda_doc.python.R;
import com.panda_doc.python.conf.Conf;
import com.panda_doc.python.util.NumberUtil;
import com.panda_doc.python.view_model.UserInfoViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

public class DocNoteFragment extends Fragment {

    WebView webView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_doc_note, container, false);

        /** 笔记列表*/
        webView = (WebView) root.findViewById(R.id.doc_note_web_view);
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
            Log.e(Conf.DOMAIN, " getCurrentPageNote isInt false");
            return;
        }
        String url = Conf.URL_NOTE_GET + viewModel.getCurrentPageNum().get();
        webView.loadUrl(url);
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
