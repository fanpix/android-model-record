package com.fanpics.opensource.android.modelrecord.callback;

import android.os.Handler;

import com.fanpics.opensource.android.modelrecord.HttpReport;
import com.fanpics.opensource.android.modelrecord.RecordCache;
import com.fanpics.opensource.android.modelrecord.configuration.BaseRecordConfiguration;
import com.fanpics.opensource.android.modelrecord.configuration.SingleRecordConfiguration;
import com.fanpics.opensource.android.modelrecord.event.EventProcessor;
import com.fanpics.opensource.android.modelrecord.event.SuccessEvent;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RecordCallback<T> extends BaseRecordCallback implements Callback<T> {

    protected SingleRecordConfiguration<T> configuration;
    protected Object key;

    protected RecordCallback(SingleRecordConfiguration<T> configuration, EventProcessor eventProcessor, HttpReport httpReport){
        super(eventProcessor, httpReport);
        this.configuration = configuration;
    }

    public RecordCallback(SingleRecordConfiguration<T> configuration, EventProcessor bus, HttpReport httpReport, Handler handler) {
        super(bus, httpReport, handler);
        this.configuration = configuration;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public Object getKey() {
        return key;
    }

    @Override
    public void success(T model, Response response) {
        success(model, response, true);
    }

    public void synchronousSuccess(T model, Response response) {
        success(model, response, false);
    }

    private void success(T model, Response response, boolean shouldPostResult) {
        final SuccessEvent<T> event = configuration.getSuccessEvent();

        event.setResult(model);
        event.setHasFinished(true);
        runCacheThread(model);
        sendHttpReport(response);
        configuration.callSuccessCallback(model);

        if (shouldPostResult) {
            postSuccessEvent(event);
        }
    }

    protected void runCacheThread(final T model) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                manageCacheIfExists(model);
            }
        }).start();
    }

    protected void manageCacheIfExists(T model) {
        final RecordCache<T> cache = configuration.getCache();
        if (cache != null) {
            cache.store(key, model);
        }
    }

    @Override
    public void failure(RetrofitError retrofitError) {
        postFailure(retrofitError);
    }

    @Override
    protected BaseRecordConfiguration getRecordConfiguration() {
        return configuration;
    }
}
