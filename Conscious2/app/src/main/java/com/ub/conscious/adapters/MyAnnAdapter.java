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

import org.w3c.dom.Text;

public class MyAnnAdapter extends ArrayAdapter<Announcement> {
    private final Activity context;
    private final Announcement[] items;

    public MyAnnAdapter(Activity context, Announcement[] items){
        super(context, R.layout.my_ann_row, items);
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.my_ann_row, null, true);
        TextView title = rowView.findViewById(R.id.title);
        TextView event = rowView.findViewById(R.id.event);
        TextView city = rowView.findViewById(R.id.city);
        TextView active = rowView.findViewById(R.id.active);

        title.setText(items[position].getTitle());
        event.setText(items[position].getEvent());
        city.setText(items[position].getCity());
        if(items[position].isBanned()){
            active.setText("Yasaklı");
        }else{
            active.setText("Aktif");
        }
        return rowView;
    }
}
