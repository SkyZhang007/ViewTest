package com.sky.viewtest;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sky.viewtest.utils.ScreenUtils;
import com.sky.viewtest.widget.HorizontalScrollViewEx;
import com.sky.viewtest.widget.HorizontalScrollViewEx2;
import com.sky.viewtest.widget.MyListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuetu-develop on 2017/11/24.
 */

public class CollisionViewActivity extends AppCompatActivity {

    private HorizontalScrollViewEx2 mListContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conllision_view);
        mListContainer = (HorizontalScrollViewEx2) findViewById(R.id.container);
        // 添加三个 listView 并添加到 HorizontalScrollViewEx
        for (int i = 0; i < 3; i++) {
            MyListView listView = new MyListView(this,null);
            List<String> list =  new ArrayList<>();
            for (int i1 = 0; i1 < 50; i1++) {
                list.add("page" + i + ", name: " + i1);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
            listView.setAdapter(adapter);
            mListContainer.addView(listView);
        }
    }



}
