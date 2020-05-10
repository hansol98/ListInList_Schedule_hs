package com.example.hansol.listinlist2;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MainSpot extends AppCompatActivity {

    MainSpotRecyclerviewAdapter adapter;
    ArrayList<String> name = new ArrayList<>();
    private List<MainActivity.Recycler_item> items = new ArrayList<MainActivity.Recycler_item>();

    private ArrayList<String > mySpot = new ArrayList<String >();

    private DbOpenHelper mDbOpenHelper;
    String sort = "userid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_spot);

        RecyclerView recyclerView = findViewById(R.id.main_spot_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MainSpotRecyclerviewAdapter(name, items);
        recyclerView.setAdapter(adapter);

        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();

        showDatabase(sort);

        // 리사이클러뷰 헤더
        name.add("첫번째");
        name.add("두번째");
        name.add("세번째");
        name.add("네번째");

        for (int i = 0 ; i < mySpot.size() ; i++) {
            items.add(new MainActivity.Recycler_item("", mySpot.get(i), "1234"));
        }

//        //리사이클러뷰 안 리서이클러뷰에 들어갈 데이터
//        items.add(new MainActivity.Recycler_item("", "경복궁", "1234"));
//        items.add(new MainActivity.Recycler_item("", "창덕궁", "2345"));
//        items.add(new MainActivity.Recycler_item("", "덕수궁", "3456"));
//        items.add(new MainActivity.Recycler_item("", "창경궁", "4567"));
    }

    public void showDatabase(String sort){
        Cursor iCursor = mDbOpenHelper.selectColumns();
        //iCursor.moveToFirst();
        Log.d("showDatabase", "DB Size: " + iCursor.getCount());
        mySpot.clear();

        while(iCursor.moveToNext()){
            String tempName = iCursor.getString(iCursor.getColumnIndex("name"));
            tempName = setTextLength(tempName,10);

            mySpot.add(tempName);
        }
    }

    public String setTextLength(String text, int length){
        if(text.length()<length){
            int gap = length - text.length();
            for (int i=0; i<gap; i++){
                text = text + " ";
            }
        }
        return text;
    }
}
