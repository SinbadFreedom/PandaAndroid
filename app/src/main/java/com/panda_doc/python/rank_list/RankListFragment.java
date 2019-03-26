package com.panda_doc.python.rank_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.panda_doc.python.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public class RankListFragment extends Fragment {
    RankListAdapter rankListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_rank_list, container, false);

        rankListAdapter = new RankListAdapter();
        /** 排行榜*/
        ListView listView = (ListView) root.findViewById(R.id.rank_list);
        listView.setAdapter(rankListAdapter);

        BottomNavigationView navigation = (BottomNavigationView) root.findViewById(R.id.rank_list_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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
            }
            return false;
        }
    };
}
