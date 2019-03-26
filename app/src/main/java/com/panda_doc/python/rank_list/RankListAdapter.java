package com.panda_doc.python.rank_list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.panda_doc.python.R;

import java.util.ArrayList;

public class RankListAdapter extends BaseAdapter {

    ArrayList<RankPojo> rankList = new ArrayList<>();

    @Override
    public int getCount() {
        return rankList.size();
    }

    @Override
    public Object getItem(int position) {
        return rankList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            rowView = inflater.inflate(R.layout.rank_list_item, parent, false);
        } else {
            rowView = convertView;
        }

        /** 设置标题数字和内容*/
        RankPojo rankPojo = (RankPojo) getItem(position);
        //TODO rank info
        return rowView;
    }
}
