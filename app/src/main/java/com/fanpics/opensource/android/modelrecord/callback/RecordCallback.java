package com.fanpics.opensource.android.modelrecord.callback;

import android.os.Handler;

import com.fanpics.opensource.android.modelrecord.HttpReport;
import com.fanpics.opensource.android.modelrecord.RecordCache;
import com.fanpics.opensource.android.modelrecord.event.SuccessEvent;
import com.fanpics.opensource.android.modelrecord.settings.BaseRecordSettings;
import com.fanpics.opensource.android.modelrecord.settings.SingleRecordSettings;
import com.squareup.otto.Bus;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RecordCallback<T> extends BaseRecordCallback implements Callback<T> {

    protected SingleRecordSettings<T> settings;
    protected Object key;

    protected RecordCallback(SingleRecordSettings<T> settings, Bus bus, HttpReport httpReport){
        super(bus, httpReport);
        this.settings = settings;
    }

    public RecordCallback(SingleRecordSettings<T> settings, Bus bus, HttpReport httpReport, Handler handler) {
        super(bus, httpReport, handler);
        this.settings = settings;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    @Override
    public void success(T model, Response response) {
        success(model, response, true);
    }

    public void synchronousSuccess(T model, Response response) {
        success(model, response, false);
    }

    private void success(T model, Response response, boolean shouldPostResult) {
        final SuccessEvent<T> event = settings.getSuccessEvent();

        event.setResult(model);
        event.setHasFinished(true);
        cacheIfExists(model);
        sendHttpReport(response);
        settings.callSuccessCallback(model);

        if (shouldPostResult) {
            postSuccessEvent(event);
        }
    }

    protected void cacheIfExists(final T model) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final RecordCache<T> cache = settings.getCache();
                if (cache != null) {
                    cache.store(key, model);
                }
            }
        }).start();
    }

    @Override
    public void failure(RetrofitError retrofitError) {
        postFailure(retrofitError);
    }

    @Override
    protected BaseRecordSettings getRecordCallbackSettings() {
        return settings;
    }
}
