package com.billcao.ichoosewho;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.billcao.page.Page;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CongressionalViewActivity extends AppCompatActivity {

    private TextView locationView;
    public ArrayList<Page> pages = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.congressional_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

    if (extras != null) {
        String repDataString = extras.getString("REPDATA");
        try {
            Gson gson = new Gson();
            Type pageArrayType = new TypeToken<ArrayList<Page>>(){}.getType();
            pages = gson.fromJson(repDataString, pageArrayType);
        } catch(Exception e) {
            Log.e("CongressionalView fail", e.toString());
        }
    }

    mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
    mRecyclerView.setHasFixedSize(true);
    mLayoutManager = new LinearLayoutManager(this);
    mRecyclerView.setLayoutManager(mLayoutManager);
    mAdapter = new CardViewDataAdapter(pages);
    mRecyclerView.setAdapter(mAdapter);
    }
}
