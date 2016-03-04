package com.billcao.ichoosewho;

import android.content.Intent;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

public class WatchListenerService extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        // TODO: Received message containing Congressional Representatives data, parse and fill activity
        if (messageEvent.getPath().equalsIgnoreCase("/ZIP")) {
            String zipCode = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("ZIP_CODE", zipCode);
            startActivity(intent);
        } else {
            super.onMessageReceived(messageEvent);
        }
    }
}
