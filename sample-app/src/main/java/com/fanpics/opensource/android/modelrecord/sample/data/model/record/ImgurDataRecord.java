package com.fanpics.opensource.android.modelrecord.sample.data.model.record;

import android.content.Context;

import com.fanpics.opensource.android.modelrecord.ModelRecord;
import com.fanpics.opensource.android.modelrecord.RecordCache;
import com.fanpics.opensource.android.modelrecord.callback.LoadCallback;
import com.fanpics.opensource.android.modelrecord.sample.data.cache.ImgurDataCache;
import com.fanpics.opensource.android.modelrecord.sample.data.model.ImgurData;
import com.fanpics.opensource.android.modelrecord.sample.data.network.ImgurItemService;
import com.fanpics.opensource.android.modelrecord.sample.event.ImgurDataLoadFailedEvent;
import com.fanpics.opensource.android.modelrecord.sample.event.ImgurDataLoadSucceededEvent;
import com.fanpics.opensource.android.modelrecord.settings.SingleRecordSettings;
import com.squareup.otto.Bus;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

public class ImgurDataRecord extends ModelRecord<ImgurData> {
    private final Context context;

    public ImgurDataRecord(Context context, Bus bus) {
        super(bus);
        this.context = context;
    }

    public void load() {
        load(null);
    }

    public void refresh() {
        refresh(null);
    }

    @Override
    protected SingleRecordSettings setupLoadSettings(SingleRecordSettings recordCallbackSettings, Object key) {
        recordCallbackSettings.setSuccessEvent(new ImgurDataLoadSucceededEvent());
        recordCallbackSettings.setFailureEvent(new ImgurDataLoadFailedEvent());
        recordCallbackSettings.setCache(createCache());
        return recordCallbackSettings;
    }

    private RecordCache createCache() {
        return new ImgurDataCache(context);
    }

    @Override
    protected void loadOnServerAsynchronously(Object key, LoadCallback loadCallback) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://api.imgur.com/3/")
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Authorization", "Client-ID df22244d34314dc");
                    }
                })
                .build();

        ImgurItemService service = restAdapter.create(ImgurItemService.class);
        service.listGallery(10, loadCallback);
    }
}
