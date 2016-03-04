package com.billcao.ichoosewho;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.wearable.view.FragmentGridPagerAdapter;

import java.util.List;

public class MainViewWatchAdapter extends FragmentGridPagerAdapter{

    private final Context mContext;
    private List mRows;
    // TODO: pages will contain Congressional rep data + 2012 Vote View data
    // TODO: Need proper data structures
    // TODO: Can also make these CardFragments in MainActivity
    private Page[] pages = {
            new Page("Dianne Feinstein", "Democrat", "Senator"),
            new Page("Barbara Boxer", "Democrat", "Senator"),
            new Page("Barbara Lee", "Democrat", "House Representative"),
            new Page("2012 Presidential Election", "Obama (87.5%) vs. Romney (9.0%)", "Alameda County, CA 13th Congressional District")};

    public MainViewWatchAdapter(Context ctx, FragmentManager fm, boolean random, boolean start) {
        super(fm);
        mContext = ctx;
        if (random) {
            pages = new Page[] {
                    new Page("Dianne Feinstein", "RANDOM", "RANDOM"),
                    new Page("Barbara Boxer", "RANDOM", "RANDOM"),
                    new Page("Barbara Lee", "RANDOM", "RANDOM"),
                    new Page("2012 Presidential Election", "Obama (17.5%) vs. Romney (81.0%)", "Conservative County, TX 19th Congressional District")};
        }
        if (start) {
            pages = new Page[] {
                    new Page("Name", "Party", "Senator/House Rep")
            };
        }
    }

    // TODO: Replace with general data structure for rep data. Sync with dataItem
    private static class Page {
        String name;
        String party; // Democrat, Republican, or Independent
        String type; // Senator or House Representative
        public Page(String n, String p, String t) {
            name = n;
            party = p;
            type = t;
        }
    }

    // TODO: Fill this out with API call, make code less hacky when adding this onto pages
    private static class Vote2012 {

    }

    @Override
    public Fragment getFragment(int row, int col) {
        Page page = pages[row];
        Fragment fragment = new RepCard(page.name, page.party, page.type);
        return fragment;
    }


    @Override
    public int getRowCount() {
        return pages.length;
    }

    // Want 1D vertical grid, so set # cols to be 1
    @Override
    public int getColumnCount(int rowNum) {
        return 1;
    }
}
