package com.billcao.ichoosewho;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class WatchToPhoneService extends Service {

    private static final String LOAD_DETAILED_VIEW_CAPABILITY_NAME = "detailed_view";
    private GoogleApiClient mWatchApiClient;

    @Override
    public void onCreate() {
        super.onCreate();

        mWatchApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {

                    }
                    @Override
                    public void onConnectionSuspended(int cause) {

                    }
                })
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWatchApiClient.disconnect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO: Less hacky way of getting data to phone
        final Bundle extras = intent.getExtras();

        new Thread(new Runnable() {
            @Override
            public void run() {
                mWatchApiClient.connect();

                if (extras.get("RANDOM") != null) {
                    Log.e("WATCHTOPHONE", "SHAKE DETECTED");
                    sendMessage("/RANDOM", "");
                } else {
                    // TODO: sendMessage to watch containing Congressional Representatives data
                    // OR use DataItem? Might just be easier to send over data and have watch parse each time
                    // TODO: Data structure, how to populate? Declare where?
                    Log.e("WATCHTOPHONESERVICE", "WORKING");
                    String repJson = (String) extras.get("REPJSON");
                    Log.e("WATCHTOPHONE", repJson);
                    sendMessage("REPJSON", repJson);
                }

            }
        }).start();

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendMessage(final String path, final String text) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("sendMessage", "run");
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mWatchApiClient).await();

                for (Node node : nodes.getNodes()) {
                    Log.e("NODE FOUND", "sendMessage");
                    try {
                        MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                                mWatchApiClient, node.getId(), path, text.getBytes()).await();
                        if (!result.getStatus().isSuccess()) {
                            Log.e("", "ERROR: failed to send Message: " + result.getStatus());
                        } else {
                            Log.e("", "SUCCESS: successfully sent send Message: " + result.getStatus());
                        }
                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }

                }
            }
        }).start();
    }
}
