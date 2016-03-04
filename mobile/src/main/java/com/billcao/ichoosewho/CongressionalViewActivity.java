package com.billcao.ichoosewho;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

// TODO: Save state, i.e. when user clicks back from detailed view all data is still present
// See https://developer.android.com/guide/components/activities.html
public class CongressionalViewActivity extends AppCompatActivity {

    private TextView locationView;
    public Page[] pages;

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

//        locationView = (TextView) findViewById(R.id.location);
//
//        if (extras != null) {
//            String zipCode = extras.getString("ZIP_CODE");
//            locationView.setText("Results for " + zipCode);
//        }

        // TODO: API calls to get actual Congressional Rep data based off of zipCode or currentLocation

        // If shaken watch, load different set
        if (extras.get("RANDOM") != null) {
            Log.e("CONGRESSIONALVIEW", "SHAKE DETECTED");
            pages = new Page[] {
                    new Page("Dianne Feinstein", "RANDOM", "RANDOM"),
                    new Page("Barbara Boxer", "RANDOM", "RANDOM"),
                    new Page("Barbara Lee", "RANDOM ", "RANDOM")};
        } else {
            pages = new Page[] {
                    new Page("Dianne Feinstein", "Democrat", "Senator"),
                    new Page("Barbara Boxer", "Democrat", "Senator"),
                    new Page("Barbara Lee", "Democrat", "House Representative")};
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // getSupportActionBar().setIcon(R.drawable.ic_launcher);

        // getSupportActionBar().setTitle("Android Versions");

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new CardViewDataAdapter(pages);
        mRecyclerView.setAdapter(mAdapter);
    }
}
