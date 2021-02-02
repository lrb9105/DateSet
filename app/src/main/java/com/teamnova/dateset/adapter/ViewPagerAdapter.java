package com.teamnova.dateset.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.teamnova.dateset.R;

import java.util.ArrayList;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.MyViewHolder> {
    private ArrayList<Bitmap> mImgList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView_post_img);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ViewPagerAdapter(ArrayList<Bitmap> mImgList) {
        this.mImgList = mImgList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewPagerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_image, parent, false);
        ViewPagerAdapter.MyViewHolder vh = new ViewPagerAdapter.MyViewHolder(itemView);
        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewPagerAdapter.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Bitmap img = mImgList.get(position);

        holder.imageView.setImageBitmap(img);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mImgList.size();
    }
}
