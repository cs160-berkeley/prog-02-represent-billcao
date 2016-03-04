package com.billcao.ichoosewho;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class CardViewDataAdapter extends RecyclerView.Adapter<CardViewDataAdapter.ViewHolder> {

    // TODO: Have final Context variable to make code cleaner
    private Page[] pages;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        public ImageView rep_image;
        public TextView name_text;
        public TextView party_text;
        public TextView type_text;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            name_text = (TextView) itemLayoutView
                    .findViewById(R.id.name_text);
            party_text = (TextView) itemLayoutView
                    .findViewById(R.id.party_text);
            type_text = (TextView) itemLayoutView
                    .findViewById(R.id.type_text);
            rep_image = (ImageView) itemLayoutView.findViewById(R.id.rep_image);

        }
    }

    public CardViewDataAdapter(Page[] data) {
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
                // TextView t = (TextView) v;
                // TODO: Less hacky way of accessing current page clicked
                Log.e("VIEW", v.toString());
                Log.e("CARDCLICKED", "CONGRESSIONAL");
                // TODO: Use DataAPI to send Page over
                Page currentPage = pages[p];
                Intent selectRepIntent = new Intent(v.getContext(), DetailedViewActivity.class);
                selectRepIntent.putExtra("/REP_NAME", currentPage.name);
                selectRepIntent.putExtra("/REP_PARTY", currentPage.party);
                selectRepIntent.putExtra("/REP_TYPE", currentPage.type);
                v.getContext().startActivity(selectRepIntent);
            }
        });
        String name = pages[position].name;
        viewHolder.name_text.setText(name);
        viewHolder.party_text.setText(pages[position].party);
        viewHolder.type_text.setText(pages[position].type);
        // Barbara Boxer becomes barbara_boxer, which is name of drawable file
        String uri = "@drawable/" + TextUtils.join("_", name.split(" ")).toLowerCase();
        int imgResource = viewHolder.rep_image.getContext().getResources().getIdentifier(uri, null, viewHolder.rep_image.getContext().getPackageName());
        Drawable res = viewHolder.rep_image.getContext().getResources().getDrawable(imgResource);
        viewHolder.rep_image.setImageDrawable(res);
    }

    @Override
    public int getItemCount() {
        return pages.length;
    }

}
