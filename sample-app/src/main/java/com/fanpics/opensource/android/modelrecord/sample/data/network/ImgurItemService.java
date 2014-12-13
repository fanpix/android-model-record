package com.fanpics.opensource.android.modelrecord.sample.data.network;

import com.fanpics.opensource.android.modelrecord.sample.data.model.ImgurData;
import com.fanpics.opensource.android.modelrecord.sample.data.model.ImgurItem;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface ImgurItemService {
    @GET("/gallery.json")
    void listGallery(@Query("perPage") int requestedCount, Callback<ImgurData> callback);

}
