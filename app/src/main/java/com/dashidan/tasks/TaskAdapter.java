package com.dashidan.tasks;

import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dashidan.R;
import com.dashidan.conf.Conf;

import java.util.ArrayList;

import androidx.drawerlayout.widget.DrawerLayout;

public class TaskAdapter extends BaseAdapter {
    ArrayList<Title> titles;
    TasksFragment tasksFragment;
    DrawerLayout mDrawerLayout;

    public TaskAdapter(DrawerLayout mDrawerLayout, TasksFragment tasksFragment) {
        titles = new ArrayList<>();
        this.tasksFragment = tasksFragment;
        this.mDrawerLayout = mDrawerLayout;
    }

    public void setContents(ArrayList<String> contents) {
        ArrayList<Title> titles = new ArrayList<>();
        for (String str : contents) {
            /** 分离标题数字和内容*/
            String trimStr = str.trim();
            String[] strarr = trimStr.split(" ");
            if (strarr.length > 0) {
                String titleNum = strarr[0];
                /** 补充标题前边的空格*/
                String[] temp = str.split(titleNum);
                if (temp.length == 2) {
                    titleNum = temp[0] + titleNum;
                } else {
                    Log.e(Conf.LOG_TAG, " temp.length != 2 str " + str + " titleNum " + titleNum);
                }
                String titleContent = str.replace(titleNum + " ", "");
                Title title = new Title(titleNum, titleContent);
                titles.add(title);
            } else {
                Log.e(Conf.LOG_TAG, "setContents strarr.length == 0 str " + str);
            }
        }
        this.titles = titles;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public Title getItem(int position) {
        return titles.get(position);
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

        /** 设置标题数字和内容*/
        final Title task = getItem(position);
        TextView titleNum = (TextView) rowView.findViewById(R.id.title_num);
        setTextViewFontType(titleNum, task.getTitleRank());
        titleNum.setText(task.getTitleNum());
        TextView titleContent = (TextView) rowView.findViewById(R.id.title_content);
        setTextViewFontType(titleContent, task.getTitleRank());
        titleContent.setText(task.getTitleContent());

        return rowView;
    }

    private void setTextViewFontType(TextView textView, int rank) {
        switch (rank) {
            case 0:
            case 1:
                textView.setTypeface(Typeface.DEFAULT_BOLD);
                textView.setTextSize(18);
                break;
            case 2:
                textView.setTypeface(Typeface.DEFAULT);
                textView.setTextSize(16);
                break;
            case 3:
                Typeface tf = Typeface.create(textView.getTypeface(), Typeface.ITALIC);
                textView.setTypeface(tf);
                textView.setTextSize(14);
                break;
            case 4:
                tf = Typeface.create(textView.getTypeface(), Typeface.ITALIC);
                textView.setTextSize(12);
                break;
            default:
                Log.e(Conf.LOG_TAG, " setTextViewFontType rank " + rank);
                break;
        }
    }
}


