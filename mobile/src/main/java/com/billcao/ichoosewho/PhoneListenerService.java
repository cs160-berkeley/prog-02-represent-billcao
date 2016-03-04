package com.billcao.ichoosewho;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

public class PhoneListenerService extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.e("PHONELISTENERSERVICE", "WORKING");
        String mData = new String(messageEvent.getData(), StandardCharsets.UTF_8);
        Log.e("mData", mData);
        // Received message about which representative is selected
        // TODO: Launch new activity (i.e. load respective activity) for selected representative (detailed view)
        // TODO: Handle case where watch is shook. Need to load Congressional View Activity with random location
        if (messageEvent.getPath().equalsIgnoreCase("/REP_NAME")) {
            Log.e("PHONELISTENER", "REP_NAME");
            //String repName = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            // TODO: Less hacky way of getting data from watch to phone
            String data = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            String[] arr = data.split(",");
            Log.e("arr", arr.toString());
            Intent intent = new Intent(this, DetailedViewActivity.class);
            // intent.putExtra("/REP_NAME", repName);
            intent.putExtra("/REP_NAME", arr[0]);
            intent.putExtra("/REP_PARTY", arr[1]);
            intent.putExtra("/REP_TYPE", arr[2]);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (messageEvent.getPath().equalsIgnoreCase("/RANDOM")) {
            Log.e("PHONELISTENER", "SHAKE DETECTED");
            Intent intent = new Intent(this, CongressionalViewActivity.class);
            intent.putExtra("RANDOM", "ZIP");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Log.e("PHONELISTENER", "FAILED PATH FIND" + messageEvent.toString());
            super.onMessageReceived(messageEvent);
        }

    }
}
