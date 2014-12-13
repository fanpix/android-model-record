package com.fanpics.opensource.android.modelrecord.sample.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ImgurData {
    @SerializedName("data")
    List<ImgurItem> imgurItems;

    public List<ImgurItem> getFirstTenItems() {
        if (imgurItems != null && imgurItems.size() > 10) {
            return imgurItems.subList(0, 10);
        }

        return imgurItems;
    }

    public void setImgurItems(List<ImgurItem> imgurItems) {
        this.imgurItems = imgurItems;
    }
}
