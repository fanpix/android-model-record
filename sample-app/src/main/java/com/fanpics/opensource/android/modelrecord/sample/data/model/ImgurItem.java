package com.fanpics.opensource.android.modelrecord.sample.data.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class ImgurItem extends RealmObject {
    private String title;

    @SerializedName("link")
    private String imageUrl;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static void cloneInto(ImgurItem cloneFrom, ImgurItem cloneTo) {
        cloneTo.setTitle(cloneFrom.getTitle());
        cloneTo.setImageUrl(cloneFrom.getImageUrl());
    }
}
