package com.billcao.ichoosewho;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.billcao.page.Page;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.ion.Ion;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "nEQCw1X6v5i0TZu3vroq6VQUL";
    private static final String TWITTER_SECRET = "WShtcZ4oWihxoNJsBYfQsZfKEnMHuPRp5rn5Mt31ZMTNT2pJEB";


    private TextView zipText;
    private Button zipCodeButton;
    private Button useCurrentLocationButton;
    private Button useRandomLocationButton;
    private GoogleApiClient mGoogleApiClient;
    private TwitterLoginButton loginButton;

    // Using lat long bounding box of US to select random lat long
    // Source: https://www.quora.com/What-is-the-longitude-and-latitude-of-a-bounding-box-around-the-continental-United-States
    // Extent: (-124.848974, 24.396308) - (-66.885444, 49.384358)
    private static final double latLowBound = 24.396308;
    private static final double latHighBound = 49.384358;
    private static final double longLowBound = -124.848974;
    private static final double longHighBound = -66.885444;

    public void randomLocation() {
        String randomLat;
        String randomLong;
        Page electionPage;
        while (true) {
            randomLat = String.valueOf(latLowBound + (Math.random() * (latHighBound - latLowBound)));
            randomLong = String.valueOf(longLowBound + (Math.random() * (longHighBound - longLowBound)));
            electionPage = getCountyState(randomLat, randomLong);
            if (electionPage != null) {
                break;
            }
        }

        ArrayList<Page> repPages = getRepresentatives(randomLat, randomLong);

        Gson gson = new Gson();
        Type pageArrayType = new TypeToken<ArrayList<Page>>(){}.getType();
        String repPageData = gson.toJson(repPages, pageArrayType);

        Intent congressionalViewIntent = new Intent(getBaseContext(), CongressionalViewActivity.class);
        congressionalViewIntent.putExtra("REPDATA", repPageData);

        Intent wearMainViewIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
        repPages.add(electionPage);
        String repPageDataWatch = gson.toJson(repPages, pageArrayType);
        wearMainViewIntent.putExtra("REPDATA", repPageDataWatch);

        startActivity(congressionalViewIntent);
        startService(wearMainViewIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            if (extras.getString("RANDOM") != null) {
                try {
                    randomLocation();
                } catch (Exception e) {
                    Log.e("Random Loc failed", "Trying again");
                }

            }
        }

        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // The TwitterSession is also available through:
                // Twitter.getInstance().core.getSessionManager().getActiveSession()
                TwitterSession session = result.data;
                // TODO: Remove toast and use the TwitterSession's userID
                // with your app's user model
                String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
            }
        });


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        zipText = (TextView) findViewById(R.id.zip);
        zipCodeButton = (Button) findViewById(R.id.zip_btn);
        useCurrentLocationButton = (Button) findViewById(R.id.use_current_location_btn);
        useRandomLocationButton = (Button) findViewById(R.id.use_random_location_btn);

        useRandomLocationButton.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View v) {
               randomLocation();
           }
        });

        zipCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String zipCode = zipText.getText().toString();
                Log.e("Inputted ZipCode", zipCode);

                if (zipCode.length() != 5) {
                    Toast.makeText(getBaseContext(), "Invalid Zip Code, please try again", Toast.LENGTH_SHORT);
                } else {
                    try {
                        Page electionPage = getCountyState(zipCode);
                        ArrayList<Page> repPages = getRepresentatives(zipCode);
                        Gson gson = new Gson();
                        Type pageArrayType = new TypeToken<ArrayList<Page>>(){}.getType();
                        String repPageData = gson.toJson(repPages, pageArrayType);

                        Intent congressionalViewIntent = new Intent(getBaseContext(), CongressionalViewActivity.class);
                        congressionalViewIntent.putExtra("REPDATA", repPageData);

                        Intent wearMainViewIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
                        repPages.add(electionPage);
                        String repPageDataWatch = gson.toJson(repPages, pageArrayType);

                        wearMainViewIntent.putExtra("REPDATA", repPageDataWatch);

                        startActivity(congressionalViewIntent);
                        startService(wearMainViewIntent);
                    } catch (Exception e) {
                        Log.e("ZipCode error", e.toString());
                        Toast.makeText(getBaseContext(), "Invalid Zip Code, please try again", Toast.LENGTH_SHORT);
                    }
                }
            }
        });

        useCurrentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current location
                Log.e("CURRENT LOCATION", "BUTTON CLICKED");
                Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                String mLatitude;
                String mLongitude;
                if (mLastLocation != null) {
                    Log.e("CURRENT LOCATION", "NOT NULL");
                    mLatitude = String.valueOf(mLastLocation.getLatitude());
                    mLongitude = String.valueOf(mLastLocation.getLongitude());
                    Log.e("CURRENT LOCATION", mLatitude + ", " + mLongitude);

                    Page electionPage = getCountyState(mLatitude, mLongitude);
                    ArrayList<Page> repPages = getRepresentatives(mLatitude, mLongitude);

                    Gson gson = new Gson();
                    Type pageArrayType = new TypeToken<ArrayList<Page>>(){}.getType();
                    String repPageData = gson.toJson(repPages, pageArrayType);

                    Intent congressionalViewIntent = new Intent(getBaseContext(), CongressionalViewActivity.class);
                    congressionalViewIntent.putExtra("REPDATA", repPageData);

                    Intent wearMainViewIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
                    repPages.add(electionPage);
                    String repPageDataWatch = gson.toJson(repPages, pageArrayType);
                    //wearMainViewIntent.putExtra("INFO", info);
                    wearMainViewIntent.putExtra("REPDATA", repPageDataWatch);

                    startActivity(congressionalViewIntent);
                    startService(wearMainViewIntent);
                }

            }
        });
    }

    public ArrayList<Page> getRepresentatives(String lat, String lon) {
        String url = "https://congress.api.sunlightfoundation.com/legislators/locate?latitude=" + lat + "&longitude=" + lon + "&apikey=8d1f835ec3774742a52d4e250e65bbc9";
        try {
            JsonObject result = Ion.with(this)
                    .load(url)
                    .asJsonObject().get();

            if (result != null) {
                Log.e("REP API CALL", result.toString());
                ArrayList<Page> repPages = getRepString(new JSONObject(result.toString()));
                return repPages;
            } else {
                Log.e("REP API", "NULL");
            }
        } catch (Exception ex) {
            Log.e("getReps loc failed", ex.toString());
        }
        return null;
    }

    // TODO: Catch Exception in caller's method in case zipCode is invalid
    public ArrayList<Page> getRepresentatives(String zipCode) {
        String url = "https://congress.api.sunlightfoundation.com/legislators/locate?zip=" + zipCode + "&apikey=8d1f835ec3774742a52d4e250e65bbc9";
        try {
            JsonObject result = Ion.with(this)
                    .load(url)
                    .asJsonObject().get();

            if (result != null) {
                Log.e("REP API CALL", result.toString());
                ArrayList<Page> repPages = getRepString(new JSONObject(result.toString()));
                return repPages;
            } else {
                Log.e("REP API", "NULL");
            }
        } catch (Exception ex) {
            Log.e("getReps loc failed", ex.toString());
        }
        return null;
    }

    // Converts zipCode to lat lon because some zipCodes don't come with county/state data
    public Page getCountyState(String zipCode) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + zipCode;
        try {
            JsonObject result = Ion.with(this)
                    .load(url)
                    .asJsonObject().get();

            JSONObject json = new JSONObject(result.toString());
            JSONArray results = json.getJSONArray("results");
            Log.e("getCountyState results", results.toString());
            String lat = results.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getString("lat");
            String lon = results.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getString("lng");
            return getCountyState(lat, lon);
        } catch (Exception ex) {
            Log.e("getCountyState failed", ex.toString());
        }
        return null;
    }

    public Page getCountyState(String lat, String lon) {
        Log.e("getCountyState", lat + " " + lon);
        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lon;
        try {
            JsonObject result = Ion.with(this)
                    .load(url)
                    .asJsonObject().get();

            String countyState = getCountyStateString(new JSONObject(result.toString()));
            String countyStateString = countyState;
            Log.e("COUNTYSTATE", countyStateString);

            InputStream stream = getAssets().open("2012data.json");
            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            String jsonString = new String(buffer, "UTF-8");
            JSONObject data = new JSONObject(jsonString);
            JSONObject vote = (JSONObject) data.get(countyStateString);
            Log.e("ELECTION DATA", vote.toString());
            String voteData = vote.toString();
            Log.e("VOTE DATA", voteData);

            JSONObject voteJSON = new JSONObject(voteData);
            String obamaString = "Obama " + voteJSON.getString("obama") + "%";
            String romneyString = "Romney " + voteJSON.getString("romney") + "%";
            voteData = obamaString + ", " + romneyString;

            Page votePage = new Page("2012 Presidential Vote", voteData, countyState);
            return votePage;
        } catch (Exception ex) {
            Log.e("getCountyState failed", ex.toString());
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {}

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connResult) {}

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

    // Takes in result of reverse geolocation call and returns String "[County], [State]"
    public String getCountyStateString(JSONObject json) {
        String county = "Dummy county";
        String state = "Dummy state";
        try {
            JSONArray results = json.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject obj = results.getJSONObject(i);
                if (obj.get("address_components") != null) {
                    JSONArray components = obj.getJSONArray("address_components");
                    for (int j = 0; j < components.length(); j++) {
                        JSONObject c = components.getJSONObject(j);
                        JSONArray types = c.getJSONArray("types");
                        String typesString = types.toString();
                        if (typesString.contains("administrative_area_level_2")) {
                            county = c.getString("long_name");
                            // Log.e("COUNTY FOUND", county);
                        }
                        if (typesString.contains("administrative_area_level_1")) {
                            state = c.getString("short_name");
                            // Log.e("STATE FOUND", state);
                        }
                    }
                }
            }

        } catch (Exception e) {
            Log.e("JSON parsing failed", e.toString());
            return null;
        }

        return county + ", " + state;
    }

    // Passing bioguide_id around makes it easiest to make another API call for the rest of needed data
    // Watch: first_name, last_name, party, bioguide_id, title
    // Congressional View: first_name, last_name, title, party, oc_email, website, twitter_id, bioguide_id
    // Detailed view: first_name, last_name, party, term_end, committees served on (bioguide_id), recent bills (bioguide_id)
    public ArrayList<Page> getRepString(JSONObject json) {
        ArrayList<Page> repPages = new ArrayList<Page>();
        try {
            JSONArray results = json.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject obj = results.getJSONObject(i);
                String firstName = obj.getString("first_name");
                String lastName = obj.getString("last_name");
                String name = firstName + " " + lastName;
                String party = obj.getString("party");
                String id = obj.getString("bioguide_id");
                String type = obj.getString("title");
                String termEnd = obj.getString("term_end");
                String twitterId = obj.getString("twitter_id");
                String email = obj.getString("oc_email");
                String website = obj.getString("website");
                JSONObject rep = new JSONObject();

                switch (party) {
                    case "D":
                        party = "Democrat";
                        break;
                    case "R":
                        party = "Republican";
                        break;
                    case "I":
                        party = "Independent";
                        break;
                    default:
                        break;
                }

                String imageUrl = "https://theunitedstates.io/images/congress/225x275/" + id +".jpg";
                // Default image is pokemon
                //byte[] imageArray = BitmapFactory.decodeResource(getResources(), R.drawable.pokemon);
                byte[] imageArray = null;
                String encodedImage = null;
                try {
                    imageArray = Ion.with(this)
                            .load(imageUrl)
                            .asByteArray().get();
                    Log.e("IMAGE ARRAY LENGTH", Integer.toString(imageArray.length));
                    encodedImage = Base64.encodeToString(imageArray, Base64.DEFAULT);
                } catch(Exception e) {
                    Log.e("No profile photo found", "");
                }
                Page repPage = new Page(name, party, type, id, email, website, twitterId, termEnd, encodedImage);
                repPages.add(repPage);
                Log.e("repPage added", repPage.toString());
            }
        } catch (Exception e) {
            Log.e("getRepString failed", e.toString());
        }
        return repPages;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Make sure that the loginButton hears the result from any
        // Activity that it triggered.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

}
