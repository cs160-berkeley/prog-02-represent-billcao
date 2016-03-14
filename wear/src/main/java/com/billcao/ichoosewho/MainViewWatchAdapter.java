package com.billcao.ichoosewho;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.util.Log;

import com.billcao.page.Page;

import java.util.ArrayList;
import java.util.List;

public class MainViewWatchAdapter extends FragmentGridPagerAdapter{

    private final Context mContext;
    private List mRows;
    private ArrayList<Page> pages;

    public MainViewWatchAdapter(Context ctx, FragmentManager fm, ArrayList<Page> p) {
        super(fm);
        mContext = ctx;
        pages = p;
    }

    @Override
    public Fragment getFragment(int row, int col) {
        Fragment fragment = null;
        if (pages.size() != 0) {
            Page page = pages.get(row);
            // Last card will always be election card, so use election card fragment
            if (row != (pages.size() - 1)) {
                fragment = new RepCard(page);
            } else {
                fragment = new ElectionCard(page);
            }
        }
        return fragment;
    }

    @Override
    public int getRowCount() {
        return pages.size();
    }

    // Want 1D vertical grid, so set # cols to be 1
    @Override
    public int getColumnCount(int rowNum) {
        return 1;
    }
}
