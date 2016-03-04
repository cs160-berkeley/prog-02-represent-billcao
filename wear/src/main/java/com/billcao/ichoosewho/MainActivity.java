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

public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private static final int SHAKE_THRESHOLD = 800;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // Don't start until msg (zip code/current location) has been passed in
        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
        pager.setAdapter(new MainViewWatchAdapter(this, getFragmentManager(), false, false));
//        else {
//            pager.setAdapter(new MainViewWatchAdapter(this, getFragmentManager(), false, true));
//        }

//        if (extras != null) {
//            String zipCode = extras.getString("ZIP_CODE");
//            // TODO: API call to get Congressional Representatives and 2012 Vote data based on zipCode or currentLocation
//            TextView zipCodeView = (TextView) findViewById(R.id.text);
//            zipCodeView.setText(zipCode);
//        }
//
//        Button testButton = (Button) findViewById(R.id.test_btn);
//
//        testButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.e("TESTBUTTON", "WORKING");
//                Intent selectRepIntent = new Intent(getBaseContext(), WatchToPhoneService.class);
//                startService(selectRepIntent);
//            }
//        });



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
                randomZipIntent.putExtra("RANDOM", "ZIP");
                startService(randomZipIntent);
                final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
                pager.setAdapter(new MainViewWatchAdapter(this, getFragmentManager(), true, false));
            }
            last_x = x;
            last_y = y;
            last_z = z;
        }
    }
}
