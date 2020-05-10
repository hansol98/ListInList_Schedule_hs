package com.example.hansol.listinlist2;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private String[] stay = new String[100];  // 하트의 클릭 여부

    private Context context;
    private List<MainActivity.Recycler_item> items;  //리사이클러뷰 안에 들어갈 값 저장
    private OnItemClick mCallback;

    //메인에서 불러올 때, 이 함수를 씀
    public MainAdapter(Context context, List<MainActivity.Recycler_item> items, OnItemClick mCallback) {
        this.context=context;
        this.items=items;   //리스트
        this.mCallback=mCallback;
    }

    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview,null);
        return new ViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final MainAdapter.ViewHolder holder, final int position) {
        final MainActivity.Recycler_item item=items.get(position);

        //이미지뷰에 url 이미지 넣기.
        Glide.with(context).load(item.getImage()).centerCrop().into(holder.image);
        holder.title.setText(item.getTitle());

        //하트누르면 내부 데이터에 저장
        holder.heart.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                if(stay[position]==null){
                    holder.heart.setBackgroundResource(R.drawable.ic_heart_off);
                    mCallback.make_db(item.getContentviewID(), item.getTitle());   //countId랑 title을 db에 넣으려고 함( make_db라는 인터페이스 이용)
                    mCallback.make_dialog();                                       //db에 잘 넣으면 띄우는 다이얼로그(위와 마찬가지로 인터페이스 이용
                    stay[position] = "ON";

//                    mySpot.add(item.getTitle());
//                    Intent intent = new Intent(context, MainSpot.class);
//                    intent.putExtra("mySpotText", mySpot);
                } else{
                    holder.heart.setBackgroundResource(R.drawable.ic_icon_addmy);
                    stay[position] = null;
                    Toast.makeText(context,"관심관광지를 취소했습니다",Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,item.getContentviewID(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, MainSpot.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        CardView cardview;
        Button heart;
        Button pin;


        public ViewHolder(View itemView) {
            super(itemView);
            heart = (Button)itemView.findViewById(R.id.cardview_heart);
            image=(ImageView)itemView.findViewById(R.id.image);
            title=(TextView)itemView.findViewById(R.id.title);
            cardview=(CardView)itemView.findViewById(R.id.cardview);
            pin=(Button)itemView.findViewById(R.id.cardview_pin);

        }
    }
}
