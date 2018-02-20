package com.sidewindercookie.glance;

/**
 * Created by hydrabolt on 2/20/18.
 */

public class DiscountOffer {
    private String name, offer, url;
    public DiscountOffer(String name, String offer, String url) {
        this.name = name;
        this.offer = offer;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getOffer() {
        return offer;
    }

    public String getUrl() {
        return url;
    }
}
