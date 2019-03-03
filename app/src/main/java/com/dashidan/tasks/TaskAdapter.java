package com.dashidan.tasks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dashidan.R;

import java.util.ArrayList;

import androidx.drawerlayout.widget.DrawerLayout;

public class TaskAdapter extends BaseAdapter {
    ArrayList<String> contents;
    TasksFragment tasksFragment;
    DrawerLayout mDrawerLayout;

    public TaskAdapter(DrawerLayout mDrawerLayout, TasksFragment tasksFragment) {
        contents = new ArrayList<>();
        this.tasksFragment = tasksFragment;
        this.mDrawerLayout = mDrawerLayout;
    }

    public void setContents(ArrayList<String> contents) {
        this.contents = contents;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return contents.size();
    }

    @Override
    public Object getItem(int position) {
        return contents.get(position);
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
            rowView = inflater.inflate(R.layout.task_item, parent, false);
        } else {
            rowView = convertView;
        }

        final String task = (String) getItem(position);

        TextView titleTV = (TextView) rowView.findViewById(R.id.title);
        titleTV.setText(task);
        return rowView;
    }
}

