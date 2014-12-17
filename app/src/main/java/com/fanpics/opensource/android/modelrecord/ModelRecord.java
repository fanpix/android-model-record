package com.fanpics.opensource.android.modelrecord;

import android.os.Handler;

import com.fanpics.opensource.android.modelrecord.settings.BaseRecordSettings;
import com.fanpics.opensource.android.modelrecord.callback.CreateCallback;
import com.fanpics.opensource.android.modelrecord.callback.DeleteCallback;
import com.fanpics.opensource.android.modelrecord.callback.LoadCallback;
import com.fanpics.opensource.android.modelrecord.callback.LoadListCallback;
import com.fanpics.opensource.android.modelrecord.settings.MultiRecordSettings;
import com.fanpics.opensource.android.modelrecord.settings.SingleRecordSettings;
import com.fanpics.opensource.android.modelrecord.callback.UpdateCallback;
import com.fanpics.opensource.android.modelrecord.event.SuccessEvent;
import com.squareup.otto.Bus;

import java.util.List;

public class ModelRecord<T> {
    private HttpReport httpReport;
    protected Bus bus;
    protected Handler handler;

    public ModelRecord(Bus bus, HttpReport httpReport) {
        this.bus = bus;
        this.httpReport = httpReport;
        this.handler = new Handler();
    }

    public ModelRecord(Bus bus) {
        this.bus = bus;
        this.handler = new Handler();
    }

    private void postOnMainThread(final Object event) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                bus.post(event);
            }
        });
    }

    public void create(T model){
        SingleRecordSettings settings = setupCreateSettings(new SingleRecordSettings<T>(), model);
        final CreateCallback createCallback = CreateCallback.createFromSettings(settings, bus, httpReport);
        settings.callOnServerAsync(model, createCallback);
    }

    protected SingleRecordSettings setupCreateSettings(SingleRecordSettings recordCallbackSettings, T model) {
        throw new RuntimeException("SetupCreateSettings() must be implemented before calling create");
    }

    public void update(T model){
        SingleRecordSettings settings = setupUpdateSettings(new SingleRecordSettings<T>(), model);
        final UpdateCallback updateCallback = UpdateCallback.createFromSettings(settings, bus, httpReport);
        settings.callOnServerAsync(model, updateCallback);
    }

    protected SingleRecordSettings setupUpdateSettings(SingleRecordSettings recordCallbackSettings, T model) {
        throw new RuntimeException("setupUpdateSettings() must be implemented before calling update");
    }

    public void loadList(Object key) {
        loadListAsynchronously(key, new MultiRecordSettings<T>(SingleRecordSettings.Type.LOAD));
    }

    public void refreshList(Object key) {
        loadListAsynchronously(key, new MultiRecordSettings<T>(SingleRecordSettings.Type.REFRESH));
    }

    public void getPreLoadedList(Object key) {
        loadListAsynchronously(key, new MultiRecordSettings<T>(SingleRecordSettings.Type.CACHE_ONLY));
    }

    protected void loadListAsynchronously(final Object key, final MultiRecordSettings recommendedSettings){
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadList(key, recommendedSettings);
            }
        }).start();
    }

    public List loadListSynchronously(final Object key) {
        final MultiRecordSettings settings = new MultiRecordSettings<T>(MultiRecordSettings.Type.LOAD);
        settings.setRunSynchronously();
        return loadList(key, settings);
    }

    public List refreshListSynchronously(final Object key) {
        final MultiRecordSettings settings = new MultiRecordSettings<T>(MultiRecordSettings.Type.REFRESH);
        settings.setRunSynchronously();
        return loadList(key, settings);
    }

    protected List loadList(Object key, MultiRecordSettings recommendedSettings) {
        MultiRecordSettings settings = setupLoadListSettings(recommendedSettings, key);
        if(settings.shouldLoadFromCache()) {
            final List loadedObject = loadListFromCache(key, settings);
            if (loadedObject != null && settings.shouldRunSynchronously()) {
                return loadedObject;
            }
        }

        if(settings.shouldLoadFromServer()) {
            return loadListOnServer(key, settings);
        }

        return null;
    }

    private List loadListFromCache(Object key, MultiRecordSettings settings) {
        final RecordCache cache = settings.getCache();
        final List object = cache.loadList(key);
        if (!settings.shouldRunSynchronously()) {
            postLoadedObject(object, settings);
        }

        return object;
    }

    private List loadListOnServer(Object key, MultiRecordSettings settings) {
        final LoadListCallback loadListCallback = LoadListCallback.createFromSettings(settings, bus, httpReport, handler);
        if (settings.shouldRunSynchronously()){
            final Result<List> result = settings.callOnServerSynchronously(key);
            if(!result.shouldCache()) {
                loadListCallback.disableCaching();
            }

            loadListCallback.synchronousSuccess(result.getModel(), result.getResponse());
            return result.getModel();
        } else {
            settings.callOnServerAsync(key, loadListCallback);
            return null;
        }
    }

    protected MultiRecordSettings setupLoadListSettings(MultiRecordSettings recordCallbackSettings, Object key) {
        throw new RuntimeException("setupLoadListSettings() must be implemented before calling loadList");
    }

    public void load(Object key) {
        loadAsynchronously(key, new SingleRecordSettings<T>(SingleRecordSettings.Type.LOAD));
    }

    public void refresh(Object key) {
        loadAsynchronously(key, new SingleRecordSettings<T>(SingleRecordSettings.Type.REFRESH));
    }

    public void getPreLoaded(Object key) {
        loadAsynchronously(key, new SingleRecordSettings<T>(SingleRecordSettings.Type.CACHE_ONLY));
    }

    protected void loadAsynchronously(final Object key, final SingleRecordSettings recommendedSettings){
        new Thread(new Runnable() {
            @Override
            public void run() {
                recommendedSettings.setRunAsynchronously();
                load(key, recommendedSettings);
            }
        }).start();
    }

    public Object loadSynchronously(final Object key) {
        final SingleRecordSettings settings = new SingleRecordSettings<T>(SingleRecordSettings.Type.LOAD);
        settings.setRunSynchronously();
        return load(key, settings);
    }

    protected Object load(Object key, SingleRecordSettings recommendedSettings) {
        SingleRecordSettings settings = setupLoadSettings(recommendedSettings, key);

        if (settings.shouldLoadFromCache()) {
            final Object loadedObject = loadFromCache(key, settings);
            if (loadedObject != null && settings.shouldRunSynchronously()) {
                return loadedObject;
            }
        }

        if (settings.shouldLoadFromServer()) {
            return loadOnServer(key, settings);
        }

        return null;
    }

    private Object loadFromCache(Object key, SingleRecordSettings settings) {
        final RecordCache cache = settings.getCache();
        final Object object = cache.load(key);
        if (!settings.shouldRunSynchronously()) {
            postLoadedObject(object, settings);
        }

        return object;
    }

    private Object loadOnServer(Object key, SingleRecordSettings settings) {
        final LoadCallback loadCallback = LoadCallback.createFromSettings(settings, bus, httpReport, key, handler);
        if (settings.shouldRunSynchronously()){
            final Result result = settings.callOnServerSynchronously(key);
            if(!result.shouldCache()) {
                loadCallback.disableCaching();
            }

            loadCallback.synchronousSuccess(result.getModel(), result.getResponse());
            return result.getModel();
        } else {
            settings.callOnServerAsync(key, loadCallback);
            return null;
        }
    }

    private void postLoadedObject(Object object, BaseRecordSettings settings) {
        if (object != null) {
            final SuccessEvent event = settings.getSuccessEvent();
            event.setHasFinished(!settings.shouldLoadFromServer());
            event.setResult(object);
            postOnMainThread(event);
        } else if (!settings.shouldLoadFromServer()) {
            final Object event = settings.getFailureEvent();
            postOnMainThread(event);
        }
    }

    protected SingleRecordSettings setupLoadSettings(SingleRecordSettings recordCallbackSettings, Object key) {
        throw new RuntimeException("setupLoadSettings() must be implemented before calling load");
    }

    public void delete(T model){
        SingleRecordSettings settings = setupDeleteSettings(new SingleRecordSettings(), model);
        final DeleteCallback deleteCallback = DeleteCallback.createFromSettings(settings, bus, httpReport, model);
        settings.callOnServerAsync(model, deleteCallback);
    }

    protected SingleRecordSettings setupDeleteSettings(SingleRecordSettings recordCallbackSettings, T model) {
        throw new RuntimeException("setupDeleteSettings() must be implemented before calling delete");
    }
}
