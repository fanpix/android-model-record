package com.fanpics.opensource.android.modelrecord.callback;

import android.os.Handler;

import com.fanpics.app.data.api.record.RecordCache;
import com.fanpics.app.event.SuccessEvent;
import com.fanpics.app.ui.NewRelicManager;
import com.squareup.otto.Bus;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoadListCallback<T> extends BaseRecordCallback implements Callback<List<T>> {

    protected com.fanpics.app.data.api.record.callback.MultiRecordSettings<T> settings;
    protected Object key;

    protected LoadListCallback(com.fanpics.app.data.api.record.callback.MultiRecordSettings<T> settings, Bus bus, NewRelicManager newRelicManager){
        super(bus, newRelicManager);
        this.settings = settings;
    }

    protected LoadListCallback(com.fanpics.app.data.api.record.callback.MultiRecordSettings settings, Bus bus, NewRelicManager newRelicManager, Handler handler) {
        super(bus, newRelicManager, handler);
        this.settings = settings;
    }

    public static LoadListCallback createFromSettings(com.fanpics.app.data.api.record.callback.MultiRecordSettings settings, Bus bus, NewRelicManager newRelicManager, Handler handler) {
        return new LoadListCallback(settings, bus, newRelicManager, handler);
    }

    public void setKey(Object key) {
        this.key = key;
    }

    protected void cacheIfExists(final List<T> model) {
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
    public void success(List<T> model, Response response) {
        success(model, response, true);
    }

    public void synchronousSuccess(List<T> model, Response response) {
        success(model, response, false);
    }

    private void success(List<T> models, Response response, boolean shouldPostResult) {
        final SuccessEvent<List<T>> event = settings.getSuccessEvent();

        event.setResult(models);
        event.setHasFinished(true);
        cacheIfExists(models);
        sendHttpReport(response);
        settings.callSuccessCallback(models);

        if (shouldPostResult) {
            postSuccessEvent(event);
        }
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
