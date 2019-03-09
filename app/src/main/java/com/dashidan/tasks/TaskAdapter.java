package com.dashidan.tasks;

import android.content.Context;
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
import java.util.HashSet;

import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class TaskAdapter extends BaseAdapter {

    ArrayList<Title> allTitles = new ArrayList<>();
    HashSet<String> docIndexes = new HashSet<>();

    TasksFragment tasksFragment;
    DrawerLayout mDrawerLayout;

    Context context;

    public TaskAdapter(DrawerLayout mDrawerLayout, TasksFragment tasksFragment, Context context) {
        this.tasksFragment = tasksFragment;
        this.mDrawerLayout = mDrawerLayout;
        this.context = context;
    }

    public void initContents(ArrayList<String> contents) {
        /** 当前显示的标题，包括 主标题和 点开的主标题对应的子标题，初始化只有主标题*/
        allTitles.clear();
        for (final String str : contents) {
            /** 分离标题数字和内容*/
            String trimStr = str.trim();
            String[] strarr = trimStr.split(" ");
            if (strarr.length > 0) {
                String titleNum = strarr[0];
                /** 获取文章第一个数字，统计文章总数用*/
                String docIndex = titleNum.split("\\.")[0];
                docIndexes.add(docIndex);
                /** 补充标题前边的空格*/
                String[] temp = str.split(titleNum);
                if (temp.length == 2) {
                    titleNum = temp[0] + titleNum;
                } else {
                    Log.e(Conf.LOG_TAG, " temp.length != 2 str " + str + " titleNum " + titleNum);
                }
                String titleContent = str.replace(titleNum + " ", "");
                Title title = new Title(titleNum, titleContent, str);
                this.allTitles.add(title);
            } else {
                Log.e(Conf.LOG_TAG, "initContents strarr.length == 0 str " + str);
            }
        }
        /** 更新目录内容*/
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.allTitles.size();
    }

    @Override
    public Title getItem(int position) {
        return this.allTitles.get(position);
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
        setTextViewFontType(titleNum, task);
        titleNum.setText(task.getTitleNum());

        TextView titleContent = (TextView) rowView.findViewById(R.id.title_content);
        setTextViewFontType(titleContent, task);
        titleContent.setText(task.getTitleContent());

        return rowView;
    }

    private void setTextViewFontType(TextView textView, Title title) {
        switch (title.getTitleRank()) {
            case 0:
            case 1:
                /** 1级标题*/
                textView.setTypeface(Typeface.DEFAULT_BOLD);
                textView.setTextSize(25);
                textView.setTextColor(ContextCompat.getColor(context, R.color.white));
                break;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                /** 2,3,4级标题*/
                textView.setTypeface(Typeface.DEFAULT);
                textView.setTextSize(19);
                textView.setTextColor(ContextCompat.getColor(context, R.color.white));
                break;
            default:
                Log.e(Conf.LOG_TAG, " setTextViewFontType rank " + title.getTitleRank());
                break;
        }
    }

    public int getDocCount() {
        return this.docIndexes.size();
    }
}

