package com.fanpics.opensource.android.modelrecord.sample.data.model.record;

import android.content.Context;

import com.fanpics.opensource.android.modelrecord.ModelRecord;
import com.fanpics.opensource.android.modelrecord.RecordCache;
import com.fanpics.opensource.android.modelrecord.sample.data.cache.ImgurDataCache;
import com.fanpics.opensource.android.modelrecord.sample.data.model.ImgurData;
import com.fanpics.opensource.android.modelrecord.sample.data.network.ImgurItemService;
import com.fanpics.opensource.android.modelrecord.sample.event.ImgurDataLoadFailedEvent;
import com.fanpics.opensource.android.modelrecord.sample.event.ImgurDataLoadSucceededEvent;
import com.fanpics.opensource.android.modelrecord.settings.BaseRecordSettings;
import com.fanpics.opensource.android.modelrecord.settings.SingleRecordSettings;
import com.squareup.otto.Bus;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

public class ImgurDataRecord extends ModelRecord<ImgurData> {
    private final Context context;

    public ImgurDataRecord(Context context, Bus bus) {
        super(bus);
        this.context = context;
    }

    @Override
    protected SingleRecordSettings setupLoadSettings(SingleRecordSettings recordCallbackSettings, Object key) {
        recordCallbackSettings.setSuccessEvent(new ImgurDataLoadSucceededEvent());
        recordCallbackSettings.setFailureEvent(new ImgurDataLoadFailedEvent());
        recordCallbackSettings.setCache(createCache());
        recordCallbackSettings.setAsyncServerCall(new BaseRecordSettings.AsyncServerCall() {
            @Override
            public void call(Object o, Callback callback) {
                RestAdapter restAdapter = buildRestAdapter();
                ImgurItemService service = restAdapter.create(ImgurItemService.class);
                service.listGallery(10, callback);
            }
        });
        return recordCallbackSettings;
    }

    private RestAdapter buildRestAdapter() {
        return new RestAdapter.Builder()
                .setEndpoint("https://api.imgur.com/3/")
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Authorization", "Client-ID df22244d34314dc");
                    }
                })
                .build();
    }

    private RecordCache createCache() {
        return new ImgurDataCache(context);
    }
}
