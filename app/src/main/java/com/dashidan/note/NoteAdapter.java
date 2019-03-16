package com.dashidan.note;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dashidan.R;

import java.util.ArrayList;

public class NoteAdapter extends BaseAdapter {

    ArrayList<Note> allTitles = new ArrayList<>();

    @Override
    public int getCount() {
        return allTitles.size();
    }

    @Override
    public Object getItem(int position) {
        return allTitles.get(position);
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
            rowView = inflater.inflate(R.layout.note_item, parent, false);
        } else {
            rowView = convertView;
        }

        TextView textView = rowView.findViewById(R.id.note_item_id);
        /** 设置标题数字和内容*/
        final Note note = (Note) getItem(position);
        textView.setText(note.getNoteText());
        return rowView;
    }

    public void setAllTitles(ArrayList<Note> allTitles) {
        this.allTitles = allTitles;
        this.notifyDataSetChanged();
    }
}