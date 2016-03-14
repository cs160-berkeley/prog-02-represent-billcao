package com.billcao.ichoosewho;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.wearable.view.CardFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.billcao.page.Page;
import com.google.gson.Gson;

import de.hdodenhof.circleimageview.CircleImageView;

public class ElectionCard extends CardFragment {

    TextView mTextView1, mTextView2, mTextView3;
    String name;
    String party;
    String type;
    Page page;

    public ElectionCard(String n, String p, String t) {
        this.name = n;
        this.party = p;
        this.type = t;
    }

    public ElectionCard(Page p) {
        this.page = p;
    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.election_card_fragment, container, false);

        mTextView1 = (TextView) root.findViewById(R.id.textView1);
        mTextView2 = (TextView) root.findViewById(R.id.textView2);
        mTextView3 = (TextView) root.findViewById(R.id.textView3);

        mTextView1.setText(page._name);
        mTextView2.setText(page._party);
        mTextView3.setText(page._type);
        return root;
    }
}
