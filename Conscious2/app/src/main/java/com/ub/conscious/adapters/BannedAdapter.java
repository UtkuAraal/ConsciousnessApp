package com.ub.conscious.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ub.conscious.R;
import com.ub.conscious.entity.Announcement;

public class BannedAdapter extends ArrayAdapter<Announcement> {
    private final Activity context;
    private final Announcement[] items;

    public BannedAdapter(Activity context, Announcement[] items){
        super(context, R.layout.banned_row, items);
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.banned_row, null, true);
        TextView title = rowView.findViewById(R.id.title);
        TextView event = rowView.findViewById(R.id.event);
        TextView city = rowView.findViewById(R.id.city);

        title.setText(items[position].getTitle());
        event.setText(items[position].getEvent());
        city.setText(items[position].getCity());

        return rowView;
    }
}
