package com.example.plantcareapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.plantcareapplication.Models.Wallpaper;
import com.example.plantcareapplication.R;

import java.util.List;

public class WallpaperAdapter extends BaseAdapter {

    private Context context;
    private List<Wallpaper> wallpaperList;

    public WallpaperAdapter(Context context, List<Wallpaper> wallpaperList) {
        this.context = context;
        this.wallpaperList = wallpaperList;
    }

    @Override
    public int getCount() {
        return wallpaperList.size();
    }

    @Override
    public Object getItem(int position) {
        return wallpaperList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_wallpaper, parent, false);
            holder = new ViewHolder();
            holder.wallpaperImageView = convertView.findViewById(R.id.wallpaperImageView);
            holder.titleTextView = convertView.findViewById(R.id.titleTextView);
            holder.descriptionTextView=convertView.findViewById(R.id.descriptionTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Wallpaper wallpaper = wallpaperList.get(position);
        holder.titleTextView.setText(wallpaper.getImageTitle());
        Glide.with(context)
                .load(wallpaper.getImageUrl())
                .into(holder.wallpaperImageView);

        return convertView;
    }

    private static class ViewHolder {
        ImageView wallpaperImageView;
        TextView titleTextView;
        TextView descriptionTextView;
    }
}
