package com.billcao.page;

import android.graphics.Bitmap;
import android.util.Log;

public class Page {
    public String _name;
    public String _party; // Democrat, Republican, or Independent
    public String _type; // Senator or House Representative
    public String _id;
    public String _email;
    public String _website;
    public String _twitterId;
    public String _termEnd;
    public String _profileImage;

    public Page(String name, String party, String type) {
        _name = name;
        _party = party;
        _type = type;
    }

    public Page(String name, String party, String type, String id, String email, String website, String twitterId, String termEnd) {
        _name = name;
        _party = party;
        _type = type;
        _id = id;
        _email = email;
        _website = website;
        _twitterId = twitterId;
        _termEnd = termEnd;
    }

    public Page(String name, String party, String type, String id, String email, String website, String twitterId, String termEnd, String image) {
        _name = name;
        _party = party;
        _type = type;
        _id = id;
        _email = email;
        _website = website;
        _twitterId = twitterId;
        _termEnd = termEnd;
        _profileImage = image;
    }

    public String toString() {
        return _name + " " + _party + " " + _type;
    }

    public String getProfileImage() {
        return _profileImage;
    }
}

