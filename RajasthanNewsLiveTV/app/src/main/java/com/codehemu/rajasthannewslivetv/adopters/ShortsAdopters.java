package com.codehemu.rajasthannewslivetv.adopters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.codehemu.rajasthannewslivetv.R;
import com.codehemu.rajasthannewslivetv.WebActivity;

import java.util.ArrayList;

public class ShortsAdopters extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<String> titles;
    ArrayList<String> des;
    ArrayList<String> images;
    ArrayList<String> links;

    public ShortsAdopters(Context context,  ArrayList<String> title, ArrayList<String> desc, ArrayList<String> image, ArrayList<String> link) {
        this.context = context;
        this.titles = title;
        this.des = desc;
        this.images = image;
        this.links = link;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = layoutInflater.inflate(R.layout.item_short,container,false);
        ImageView imageView  = itemView.findViewById(R.id.imageView);
        ImageView imageView1  = itemView.findViewById(R.id.imageView2);
        TextView textView =itemView.findViewById(R.id.headline);
        TextView textView1 =itemView.findViewById(R.id.desc);
        TextView textView2 =itemView.findViewById(R.id.textView6);

        textView.setText(titles.get(position));
        textView1.setText(des.get(position) + ".....");

        Glide.with(context).load(images.get(position)).centerCrop().into(imageView);
        Glide.with(context).load(images.get(position)).centerCrop().override(12,12).into(imageView1);

        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent t = new Intent(v.getContext(), WebActivity.class);
                t.putExtra("title","Short");
                t.putExtra("url",links.get(position));
                v.getContext().startActivity(t);
            }
        });

        container.addView(itemView);

        return itemView;
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }

}
