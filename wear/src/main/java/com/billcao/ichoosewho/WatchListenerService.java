package com.billcao.ichoosewho;

import android.content.Intent;
import android.util.Log;

import com.billcao.page.Page;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class WatchListenerService extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        // TODO: Received message containing Congressional Representatives data, parse and fill activity
        if (messageEvent.getPath().equalsIgnoreCase("REPDATA")) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            String repDataString = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            intent.putExtra("REPDATA", repDataString);
            startActivity(intent);
        } else if (messageEvent.getPath().equalsIgnoreCase("/INFO")) {
            Log.e("INFO PATH", "PARSING");
            // Path INFO should have data in form of [repString]|[countyState]|[voteData]
            String info = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Log.e("Info String", info);
            String[] infoArray = info.split("\\|");

            String repString = infoArray[0];
            String countyState = infoArray[1];
            String voteData = infoArray[2];

            Log.e("INFO WATCH", repString + countyState + voteData);

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            intent.putExtra("REPSTRING", repString);
            intent.putExtra("COUNTYSTATE", countyState);
            intent.putExtra("VOTEDATA", voteData);

            startActivity(intent);
        } else {
            Log.e("WatchListenerService", "No message received");
            super.onMessageReceived(messageEvent);
        }
    }
}
