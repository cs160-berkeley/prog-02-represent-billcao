package com.billcao.ichoosewho;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

public class PhoneListenerService extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equalsIgnoreCase("REPJSON")) {
            Log.e("PHONELISTENER", "REPJSON");
            String repJson = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Intent intent = new Intent(this, DetailedViewActivity.class);
            intent.putExtra("REPJSON", repJson);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (messageEvent.getPath().equalsIgnoreCase("/RANDOM")) {
            Log.e("PHONELISTENER", "SHAKE DETECTED");
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("RANDOM", "ZIP");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Log.e("PHONELISTENER", "FAILED PATH FIND" + messageEvent.toString());
            super.onMessageReceived(messageEvent);
        }

    }
}
