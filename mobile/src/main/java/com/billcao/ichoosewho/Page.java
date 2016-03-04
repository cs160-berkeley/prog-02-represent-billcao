package com.billcao.ichoosewho;

public class Page {
    String name;
    String party; // Democrat, Republican, or Independent
    String type; // Senator or House Representative
    public Page(String n, String p, String t) {
        name = n;
        party = p;
        type = t;
    }
}