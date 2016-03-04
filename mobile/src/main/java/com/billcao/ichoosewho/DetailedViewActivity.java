package com.billcao.ichoosewho;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

// TODO: Save state, i.e. when user clicks back from detailed view all data is still present
// See https://developer.android.com/guide/components/activities.html
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
            String name = (String) extras.get("/REP_NAME");
            String party = (String) extras.get("/REP_PARTY");
            String type = (String) extras.get("/REP_TYPE");
            Log.e("DETAILEDVIEW", "EXTRAS" + extras.toString());
            TextView nameView = (TextView) findViewById(R.id.name);
            nameView.setText(name);
            TextView partyView = (TextView) findViewById(R.id.party);
            partyView.setText(party);
            TextView typeView = (TextView) findViewById(R.id.type);
            typeView.setText(type);

            ImageView imgView = (ImageView) findViewById(R.id.rep_image);
            // Barbara Boxer becomes barbara_boxer, which is name of drawable file
            String uri = "@drawable/" + TextUtils.join("_", name.split(" ")).toLowerCase();
            int imgResource = getResources().getIdentifier(uri, null, getPackageName());
            Drawable res = getResources().getDrawable(imgResource);
            imgView.setImageDrawable(res);

        }
    }
}
