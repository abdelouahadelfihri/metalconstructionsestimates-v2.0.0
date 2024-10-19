package com.example.metalconstructionsestimates;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.metalconstructionsestimates.modules.customers.Customers;
import com.example.metalconstructionsestimates.dashboard.Dashboard;
import com.example.metalconstructionsestimates.modules.estimates.Estimates;
import com.example.metalconstructionsestimates.modules.steels.Steels;


public class GridAdapter extends BaseAdapter {

    Context context;
    private final String [] values;
    private final int [] images;
    LayoutInflater layoutInflater;

    public GridAdapter(Context context, String[] values, int[] images) {
        this.context = context;
        this.values = values;
        this.images = images;
    }

    @Override
    public int getCount() {
        return values.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        Holder holder = new Holder();
        View rowView;

        rowView = layoutInflater.inflate(R.layout.single_item, null);
        holder.tv = rowView.findViewById(R.id.textview);
        holder.img = rowView.findViewById(R.id.imageview);

        holder.tv.setText(values[position]);
        holder.img.setImageResource(images[position]);

        rowView.setOnClickListener(view -> {
            switch(values[position]){
                case "Dashboard":
                    Intent intent = new Intent(context, Dashboard.class);
                    context.startActivity(intent);
                    break;
                case "Estimates":
                    intent = new Intent(context, Estimates.class);
                    context.startActivity(intent);
                    break;
                case "Customers":
                    intent = new Intent(context, Customers.class);
                    context.startActivity(intent);
                    break;
                case "Steels":
                    intent = new Intent(context, Steels.class);
                    context.startActivity(intent);
                    break;
            }
        });

        return rowView;
    }

    public class Holder
    {
        TextView tv;
        ImageView img;
    }

}