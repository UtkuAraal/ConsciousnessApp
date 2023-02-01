package com.ub.conscious.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ub.conscious.Details;
import com.ub.conscious.R;
import com.ub.conscious.entity.Announcement;

public class SharedAdapter extends ArrayAdapter<Announcement> {
    public static Activity context;
    public static Announcement[] items;

    public SharedAdapter(Activity context, Announcement[] items){
        super(context, R.layout.shared_row, items);
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.shared_row, null, true);
        TextView title = rowView.findViewById(R.id.title);

        title.setText(items[position].getTitle());

        title.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Details.class);
                intent.putExtra("announcement", items[position]);
                context.startActivity(intent);
            }
        });


        return rowView;
    }

}


