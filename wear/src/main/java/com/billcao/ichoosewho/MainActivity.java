package com.billcao.ichoosewho;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.GridViewPager;
import android.util.Log;
import android.widget.Toast;

import com.billcao.page.Page;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import java.lang.reflect.Type;
import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends Activity implements SensorEventListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "nEQCw1X6v5i0TZu3vroq6VQUL";
    private static final String TWITTER_SECRET = "WShtcZ4oWihxoNJsBYfQsZfKEnMHuPRp5rn5Mt31ZMTNT2pJEB ";


    private SensorManager mSensorManager;
    private Sensor mSensor;
    private static final int SHAKE_THRESHOLD = 800;
    public ArrayList<Page> pages = new ArrayList<Page>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            String repDataString = extras.getString("REPDATA");
            Gson gson = new Gson();
            Type pageArrayType = new TypeToken<ArrayList<Page>>(){}.getType();
            pages = gson.fromJson(repDataString, pageArrayType);

        }


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
        pager.setAdapter(new MainViewWatchAdapter(this, getFragmentManager(), pages));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){}

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    long lastUpdate;
    float last_x;
    float last_y;
    float last_z;

    // Code adapted from https://stackoverflow.com/questions/5271448/how-to-detect-shake-event-with-android
    @Override
    public final void onSensorChanged(SensorEvent event) {
        // use values from event.values array
        float[] values = event.values;
        long curTime = System.currentTimeMillis();
        // only allow one update every 100ms.
        if ((curTime - lastUpdate) > 100) {
            long diffTime = (curTime - lastUpdate);
            lastUpdate = curTime;

            float x = values[SensorManager.DATA_X];
            float y = values[SensorManager.DATA_Y];
            float z = values[SensorManager.DATA_Z];

            float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

            if (speed > SHAKE_THRESHOLD) {
                Log.e("sensor", "shake detected w/ speed: " + speed);
                Toast.makeText(this, "shake detected w/ speed: " + speed, Toast.LENGTH_SHORT).show();
                Intent randomZipIntent = new Intent(getBaseContext(), WatchToPhoneService.class);
                randomZipIntent.putExtra("RANDOM", "LOCATION");
                startService(randomZipIntent);
                final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
                pager.setAdapter(new MainViewWatchAdapter(this, getFragmentManager(), pages));
            }
            last_x = x;
            last_y = y;
            last_z = z;
        }
    }
}
