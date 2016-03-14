package com.billcao.ichoosewho;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.wearable.view.CardFragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.billcao.page.Page;
import com.google.gson.Gson;

import de.hdodenhof.circleimageview.CircleImageView;

public class RepCard extends CardFragment {

    CircleImageView profileImage;
    TextView mTextView1, mTextView2;
    String name;
    String party;
    String type;
    Page page;

    public RepCard(String n, String p, String t) {
        this.name = n;
        this.party = p;
        this.type = t;
    }

    public RepCard(Page p) {
        this.page = p;
    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.rep_card_fragment, container, false);

        profileImage = (CircleImageView) root.findViewById(R.id.profile_image);
        mTextView1 = (TextView) root.findViewById(R.id.textView1);
        mTextView2 = (TextView) root.findViewById(R.id.textView2);
        if (page.getProfileImage() != null) {
            Bitmap imageMap = StringToBitMap(page.getProfileImage());
            profileImage.setImageBitmap(imageMap);
        } else {
            profileImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pokemon));
        }
        mTextView1.setText(page._name + " (" + page._party.charAt(0) + ")");
        String type = page._type;
        if (type.equals("Sen")) {
            mTextView2.setText("Senator");

        } else if (type.equals("Rep")) {
            mTextView2.setText("Representative");

        } else {
            mTextView2.setText(type);
        }


        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("CLICKED", "REP CARD");
                Intent selectRepIntent = new Intent(getActivity(), WatchToPhoneService.class);
                Gson gson = new Gson();
                selectRepIntent.putExtra("REPJSON", gson.toJson(page, Page.class));
                getActivity().startService(selectRepIntent);
            }
        });

        return root;
    }

    // https://stackoverflow.com/questions/13562429/how-many-ways-to-convert-bitmap-to-string-and-vice-versa
    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}
