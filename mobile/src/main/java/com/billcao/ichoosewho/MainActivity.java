package com.billcao.ichoosewho;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
public class MainActivity extends AppCompatActivity {

    private Button zipCodeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        zipCodeButton = (Button) findViewById(R.id.zip_btn);

        zipCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: This is the Main View for mobile. Should include Zip Code submission and use current location
                // Add onto intent zipCode or userLocation and then start CongressionalViewActivity
                // TODO: Need to send data to watch as well to load congressional reps page? (Or is this done in CongressionalViewActivity?)
                // I think you startService here (pretty sure)
                Intent congressionalViewIntent = new Intent(getBaseContext(), CongressionalViewActivity.class);
                Intent wearMainViewIntent = new Intent(getBaseContext(), PhoneToWatchService.class);

                String testZipCode = Double.toString(10000*Math.random());
                Log.d("Zip Code sent", testZipCode);
                congressionalViewIntent.putExtra("ZIP_CODE", testZipCode);
                wearMainViewIntent.putExtra("ZIP_CODE", testZipCode);
                // TODO: Add current location into intent
                startActivity(congressionalViewIntent);
                startService(wearMainViewIntent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
