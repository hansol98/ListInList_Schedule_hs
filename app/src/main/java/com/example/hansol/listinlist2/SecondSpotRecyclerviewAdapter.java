package com.example.hansol.listinlist2;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SecondSpotRecyclerviewAdapter extends RecyclerView.Adapter<SecondSpotRecyclerviewAdapter.ViewHolder> {
    Context context;
    private String[] stay = new String[5];  // 하트의 클릭 여부
    private List<MainActivity.Recycler_item> items;  //리사이클러뷰 안에 들어갈 값 저장

    public SecondSpotRecyclerviewAdapter(List<MainActivity.Recycler_item> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.second_spot_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MainActivity.Recycler_item item=items.get(position);

        holder.title.setText(items.get(position).title);
        //이미지뷰에 url 이미지 넣기.
        Glide.with(context).load(item.getImage()).centerCrop().into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView type;
        TextView title;
        Button heart;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.no_image);
            type = itemView.findViewById(R.id.cardview_type);
            title = itemView.findViewById(R.id.cardview_title);
            heart = itemView.findViewById(R.id.cardview_heart);
        }
    }
}
