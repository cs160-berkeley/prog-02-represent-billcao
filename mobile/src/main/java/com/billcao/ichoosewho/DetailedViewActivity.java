package com.billcao.ichoosewho;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.billcao.page.Page;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DetailedViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            Gson gson = new Gson();
            String repJson = extras.getString("REPJSON");
            Page rep = gson.fromJson(repJson, Page.class);

            String name = rep._name;
            String party = rep._party;
            String type = rep._type;

            TextView nameView = (TextView) findViewById(R.id.name);
            nameView.setText(type + " " + name);
            TextView partyView = (TextView) findViewById(R.id.party);
            partyView.setText(party);
            TextView endDate = (TextView) findViewById(R.id.term_end_date);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
            SimpleDateFormat newFormat = new SimpleDateFormat("MMM dd, yyyy");
            String termEnd = rep._termEnd;
            try {
                Date date = formatter.parse(termEnd);


                String formattedDate = newFormat.format(date);
                endDate.setText(formattedDate);
            } catch(Exception e) {
                e.printStackTrace();
            }

            ImageView repImage = (ImageView) findViewById(R.id.rep_image);

            String bioguideId = rep._id;
            String imageUrl = "https://theunitedstates.io/images/congress/225x275/" + bioguideId +".jpg";
            try {
                Bitmap imageMap = Ion.with(this)
                        .load(imageUrl)
                        .asBitmap().get();
                repImage.setImageBitmap(imageMap);
            } catch(Exception ex) {
                ex.printStackTrace();
            }

            ArrayList<String> comList = new ArrayList<String>();
            ListView comListView = (ListView) findViewById(R.id.committees_list);
            String getComUrl = "https://congress.api.sunlightfoundation.com/committees?member_ids=" + bioguideId + "&apikey=8d1f835ec3774742a52d4e250e65bbc9";
            try {
                JsonObject result = Ion.with(this)
                        .load(getComUrl)
                        .asJsonObject().get();

                if (result != null) {
                    // TODO: Parse output and put into DetailedView ListView
                    // Format appropriately
                    Log.e("Coms", result.toString());
                    JSONObject obj = new JSONObject(result.toString());
                    JSONArray data = obj.getJSONArray("results");
                    // Get 5 committees?
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject o = data.getJSONObject(i);
                        String comName = o.getString("name");
                        comList.add(comName);
                    }
                    ArrayAdapter<String> comAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, comList);
                    comListView.setAdapter(comAdapter);
                } else {
                    Log.e("Com API", "NULL");
                }
            } catch (Exception ex) {
                Log.e("getCom failed", ex.toString());
            }

            ArrayList<String> billsList = new ArrayList<String>();
            ListView billsListView = (ListView) findViewById(R.id.bills_list);
            String getBillsUrl = "https://congress.api.sunlightfoundation.com/bills?sponsor_id=" + bioguideId + "&apikey=8d1f835ec3774742a52d4e250e65bbc9";
            try {
                JsonObject result = Ion.with(this)
                        .load(getBillsUrl)
                        .asJsonObject().get();

                if (result != null) {
                    // TODO: Parse output and put into DetailedView ListView
                    // Format appropriately
                    Log.e("Bills", result.toString());
                    JSONObject obj = new JSONObject(result.toString());
                    JSONArray data = obj.getJSONArray("results");
                    // Limit bills?
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject o = data.getJSONObject(i);
                        try {
                            String billNameOfficial = o.getString("official_title");
                            String billNameShort = o.getString("short_title");
                            String introDate = o.getString("introduced_on");
                            Date introD = formatter.parse(introDate);
                            String introDateReadable = newFormat.format(introD);
                            if (!billNameShort.equals("null")) {
                                billsList.add(billNameShort + " (" + introDateReadable + ")");
                            } else if (!billNameOfficial.equals("null")){
                                billsList.add(billNameOfficial + " (" + introDateReadable + ")");
                            }
                            // Log.e("BILL", o.toString());
                        } catch(Exception e) {
                            Log.e("Failed to parse Bill", o.toString());
                            Log.e("E", e.toString());
                        }

                    }
                    ArrayAdapter<String> billsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, billsList);
                    billsListView.setAdapter(billsAdapter);
                } else {
                    Log.e("Bills API", "NULL");
                }
            } catch (Exception ex) {
                Log.e("getBills failed", ex.toString());
            }
        }
    }
}
