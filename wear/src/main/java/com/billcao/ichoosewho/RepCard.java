package com.billcao.ichoosewho;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.CardFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RepCard extends CardFragment {

    TextView mTextView1, mTextView2, mTextView3;
    String name;
    String party;
    String type;

    public RepCard(String n, String p, String t) {
        this.name = n;
        this.party = p;
        this.type = t;
    }

//    public static RepCard random() {
//        return new RepCard();
//    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.rep_card_fragment, container, false);

        mTextView1 = (TextView) root.findViewById(R.id.textView1);
        mTextView2 = (TextView) root.findViewById(R.id.textView2);
        mTextView3 = (TextView) root.findViewById(R.id.textView3);

        mTextView1.setText(this.name);
        mTextView2.setText(this.party);
        mTextView3.setText(this.type);

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("CLICKED", "WATCH CARD");
                Intent selectRepIntent = new Intent(getActivity(), WatchToPhoneService.class);
                selectRepIntent.putExtra("/REP_NAME", name);
                selectRepIntent.putExtra("/REP_PARTY", party);
                selectRepIntent.putExtra("/REP_TYPE", type);
                getActivity().startService(selectRepIntent);
            }
        });

        return root;
    }
}
