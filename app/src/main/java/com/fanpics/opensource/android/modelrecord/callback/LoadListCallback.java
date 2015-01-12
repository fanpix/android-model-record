package com.fanpics.opensource.android.modelrecord.callback;

import android.os.Handler;

import com.fanpics.opensource.android.modelrecord.HttpReport;
import com.fanpics.opensource.android.modelrecord.RecordCache;
import com.fanpics.opensource.android.modelrecord.configuration.BaseRecordConfiguration;
import com.fanpics.opensource.android.modelrecord.configuration.MultiRecordConfiguration;
import com.fanpics.opensource.android.modelrecord.event.EventProcessor;
import com.fanpics.opensource.android.modelrecord.event.SuccessEvent;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoadListCallback<T> extends BaseRecordCallback implements Callback<List<T>> {

    protected MultiRecordConfiguration<T> configuration;
    protected Object key;

    protected LoadListCallback(MultiRecordConfiguration<T> configuration, EventProcessor eventProcessor, HttpReport httpReport){
        super(eventProcessor, httpReport);
        this.configuration = configuration;
    }

    protected LoadListCallback(MultiRecordConfiguration<T> configuration, EventProcessor eventProcessor, HttpReport httpReport, Handler handler) {
        super(eventProcessor, httpReport, handler);
        this.configuration = configuration;
    }

    public static <T> LoadListCallback createFromConfiguration(MultiRecordConfiguration<T> settings, EventProcessor eventProcessor, HttpReport httpReport, Handler handler) {
        return new LoadListCallback<>(settings, eventProcessor, httpReport, handler);
    }

    public void setKey(Object key) {
        this.key = key;
    }

    protected void cacheIfExists(final List<T> model) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final RecordCache<T> cache = configuration.getCache();
                if (cache != null) {
                    cache.store(key, model);
                }
            }
        }).start();
    }

    @Override
    public void success(List<T> model, Response response) {
        success(model, response, true);
    }

    public void synchronousSuccess(List<T> model, Response response) {
        success(model, response, false);
    }

    private void success(List<T> models, Response response, boolean shouldPostResult) {
        final SuccessEvent<List<T>> event = configuration.getSuccessEvent();

        event.setResult(models);
        event.setHasFinished(true);
        cacheIfExists(models);
        sendHttpReport(response);
        configuration.callSuccessCallback(models);

        if (shouldPostResult) {
            postSuccessEvent(event);
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
