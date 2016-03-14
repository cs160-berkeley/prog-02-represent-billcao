package com.billcao.ichoosewho;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.billcao.page.Page;
import com.google.gson.Gson;
import com.koushikdutta.ion.Ion;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.CompactTweetView;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class CardViewDataAdapter extends RecyclerView.Adapter<CardViewDataAdapter.ViewHolder> {

    // TODO: Have final Context variable to make code cleaner
    private ArrayList<Page> pages;
    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "nEQCw1X6v5i0TZu3vroq6VQUL";
    private static final String TWITTER_SECRET = "WShtcZ4oWihxoNJsBYfQsZfKEnMHuPRp5rn5Mt31ZMTNT2pJEB";

    public static class ViewHolder extends RecyclerView.ViewHolder {

    private CardView cardView;
    public ImageView rep_image;
    public TextView name_text;
    public TextView party_text;
    public TextView email_text;
    public TextView website_text;
    public RelativeLayout infoHolder;

    public ViewHolder(View itemLayoutView) {
        super(itemLayoutView);
        cardView = (CardView) itemView.findViewById(R.id.card_view);
        name_text = (TextView) itemLayoutView
                .findViewById(R.id.name_text);
        party_text = (TextView) itemLayoutView
                .findViewById(R.id.party_text);
        email_text = (TextView) itemLayoutView
                .findViewById(R.id.email_text);
        website_text = (TextView) itemLayoutView
                .findViewById(R.id.website_text);
        rep_image = (ImageView) itemLayoutView.findViewById(R.id.rep_image);
        infoHolder = (RelativeLayout) itemLayoutView.findViewById(R.id.info_holder);

    }
}

    public CardViewDataAdapter(ArrayList<Page> data) {
        pages = data;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CardViewDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_row, null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        final int p = position;
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Page rep = pages.get(p);
                Gson gson = new Gson();
                String repJson = gson.toJson(rep);
                Log.e("repJson", repJson);
                Intent selectRepIntent = new Intent(v.getContext(), DetailedViewActivity.class);
                selectRepIntent.putExtra("REPJSON", repJson);
                v.getContext().startActivity(selectRepIntent);
            }
        });
        Page currentPage = pages.get(p);
        String name = currentPage._name;
        String party = currentPage._party;
        viewHolder.name_text.setText(currentPage._type + " " + name);
        viewHolder.party_text.setText(currentPage._party);
        String email = currentPage._email;
        String emailString = "<a href=\"mailto:" + email + "\">" + email + "</a>";
        viewHolder.email_text.setText(Html.fromHtml(emailString));
        viewHolder.email_text.setMovementMethod(LinkMovementMethod.getInstance());
        viewHolder.email_text.setTextColor(Color.rgb(255,255,255));
        String website = currentPage._website;
        String websiteString = "<a href=\"" + website + "\">" + website + "</a>";
        viewHolder.website_text.setText(Html.fromHtml(websiteString));
        viewHolder.website_text.setMovementMethod(LinkMovementMethod.getInstance());
        viewHolder.website_text.setTextColor(Color.rgb(255, 255, 255));

        // TODO: Change background color based on rep's party
        // Increase opacity for readability
        int democratBlue = Color.argb(255, 51, 102, 153);
        int republicanRed = Color.argb(255, 209, 33, 0);
        if (party.equals("Democrat")) {
            viewHolder.cardView.setCardBackgroundColor(democratBlue);
        } else if (party.equals("Republican")) {
            viewHolder.cardView.setCardBackgroundColor(republicanRed);
        }

        String bioguideId = currentPage._id;
        String imageUrl = "https://theunitedstates.io/images/congress/225x275/" + bioguideId +".jpg";
        try {
            Bitmap imageMap = Ion.with(viewHolder.cardView.getContext())
                    .load(imageUrl)
                    .asBitmap().get();
            viewHolder.rep_image.setImageBitmap(imageMap);
        } catch(Exception e) {
            e.printStackTrace();
            // Default to pokemon
            viewHolder.rep_image.setImageDrawable(viewHolder.rep_image.getContext().getResources().getDrawable(R.drawable.pokemon));
        }

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(viewHolder.cardView.getContext(), new Twitter(authConfig));

        String twitterId = currentPage._twitterId;

        if (twitterId != null) {
            TwitterApiClient client = Twitter.getApiClient();
            // Get latest tweet ID given twitterId
            String url = "https://api.twitter.com/1.1/statuses/user_timeline.json?user_id=" + twitterId + "&count=1";
            try {
                client.getStatusesService().userTimeline(null, twitterId, 1, null, null, null, null, null, null, new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> listResult) {
                        List<Tweet> d = listResult.data;
                        Tweet latestTweet = d.get(0);
                        CompactTweetView tweetView = new CompactTweetView(viewHolder.cardView.getContext(), latestTweet);
                        viewHolder.infoHolder.addView(tweetView);
                    }
                    @Override
                    public void failure(TwitterException e) {
                        e.printStackTrace();
                    }
                });
            } catch(Exception e) {
                Log.e("Error loading tweet", e.toString());
            }


        } else {
            // Say that Rep doesn't have a twitter
        }
    }

    @Override
    public int getItemCount() {
        return pages.size();
    }
}
