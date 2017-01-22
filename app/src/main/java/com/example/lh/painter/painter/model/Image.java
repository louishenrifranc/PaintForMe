package com.example.lh.painter.painter.model;

/**
 * Created by lh on 17-01-22.
 */

public class Image {
    String name;
    String url;
    String small;
    String medium;
    String large;
    String timestamp;

    public Image() {
        name = new String();
        url = new String();
        small = new String();
        medium = new String();
        large = new String();
        timestamp = new String();
    }

    public void setLarge(String large) {
        this.large = large;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLarge() {
        return large;
    }

    public String getMedium() {
        return medium;
    }

    public String getName() {
        return name;
    }

    public String getSmall() {
        return small;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getUrl() {
        return url;
    }
}

