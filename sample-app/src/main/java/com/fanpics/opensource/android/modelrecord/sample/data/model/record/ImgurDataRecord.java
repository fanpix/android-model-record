package com.fanpics.opensource.android.modelrecord.sample.data.model.record;

import android.content.Context;

import com.fanpics.opensource.android.modelrecord.ModelRecord;
import com.fanpics.opensource.android.modelrecord.RecordCache;
import com.fanpics.opensource.android.modelrecord.configuration.BaseRecordConfiguration;
import com.fanpics.opensource.android.modelrecord.configuration.SingleRecordConfiguration;
import com.fanpics.opensource.android.modelrecord.sample.data.cache.ImgurDataCache;
import com.fanpics.opensource.android.modelrecord.sample.data.model.ImgurData;
import com.fanpics.opensource.android.modelrecord.sample.data.network.ImgurItemService;
import com.fanpics.opensource.android.modelrecord.sample.event.ImgurDataLoadFailedEvent;
import com.fanpics.opensource.android.modelrecord.sample.event.ImgurDataLoadSucceededEvent;
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
    protected SingleRecordConfiguration setupLoadSettings(SingleRecordConfiguration configuration, Object key) {
        configuration.setSuccessEvent(new ImgurDataLoadSucceededEvent());
        configuration.setFailureEvent(new ImgurDataLoadFailedEvent());
        configuration.setCache(createCache());
        configuration.setAsyncServerCall(new BaseRecordConfiguration.AsyncServerCall() {
            @Override
            public void call(Object o, Callback callback) {
                RestAdapter restAdapter = buildRestAdapter();
                ImgurItemService service = restAdapter.create(ImgurItemService.class);
                service.listGallery(10, callback);
            }
        });
        return configuration;
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
