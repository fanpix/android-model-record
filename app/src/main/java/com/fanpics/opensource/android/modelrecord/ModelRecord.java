package com.fanpics.opensource.android.modelrecord;

import android.os.Handler;

import com.fanpics.app.data.api.record.callback.BaseRecordSettings;
import com.fanpics.app.data.api.record.callback.CreateCallback;
import com.fanpics.app.data.api.record.callback.DeleteCallback;
import com.fanpics.app.data.api.record.callback.LoadCallback;
import com.fanpics.app.data.api.record.callback.LoadListCallback;
import com.fanpics.app.data.api.record.callback.MultiRecordSettings;
import com.fanpics.app.data.api.record.callback.SingleRecordSettings;
import com.fanpics.app.data.api.record.callback.UpdateCallback;
import com.fanpics.app.event.SuccessEvent;
import com.fanpics.app.ui.NewRelicManager;
import com.squareup.otto.Bus;

import java.util.List;

public class ModelRecord<T> {
    private final NewRelicManager newRelicManager;
    protected Bus bus;
    protected Handler handler;

    public ModelRecord(Bus bus, NewRelicManager newRelicManager) {
        this.bus = bus;
        this.newRelicManager = newRelicManager;
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
        final CreateCallback createCallback = CreateCallback.createFromSettings(settings, bus, newRelicManager);
        createOnServer(model, createCallback);
    }

    protected void createOnServer(T model, CreateCallback createCallback) {
        throw new RuntimeException("createOnServer() must be implemented before calling create");
    }

    protected SingleRecordSettings setupCreateSettings(SingleRecordSettings recordCallbackSettings, T model) {
        throw new RuntimeException("SetupCreateSettings() must be implemented before calling create");
    }

    public void update(T model){
        SingleRecordSettings settings = setupUpdateSettings(new SingleRecordSettings<T>(), model);
        final UpdateCallback updateCallback = UpdateCallback.createFromSettings(settings, bus, newRelicManager);
        updateOnServer(model, updateCallback);
    }

    protected void updateOnServer(T model, UpdateCallback updateCallback) {
        throw new RuntimeException("updateOnServer() must be implemented before calling update");
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
        final LoadListCallback loadListCallback = LoadListCallback.createFromSettings(settings, bus, newRelicManager, handler);
        if (settings.shouldRunSynchronously()){
            final Result<List> result = loadListOnServerSynchronously(key);
            if(!result.shouldCache()) {
                loadListCallback.disableCaching();
            }

            loadListCallback.synchronousSuccess(result.getModel(), result.getResponse());
            return result.getModel();
        } else {
            loadListOnServerAsynchronously(key, loadListCallback);
            return null;
        }
    }

    protected void loadListOnServerAsynchronously(Object key, LoadListCallback loadListCallback) {
        throw new RuntimeException("loadListOnServerAsynchronously() must be implemented before calling loadList with settings set to asynchronous");
    }

    protected Result<List> loadListOnServerSynchronously(Object key) {
        throw new RuntimeException("loadListOnServerSynchronously() must be implemented before calling loadList with settings set to synchronous");
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
        final LoadCallback loadCallback = LoadCallback.createFromSettings(settings, bus, newRelicManager, key, handler);
        if (settings.shouldRunSynchronously()){
            final Result result = loadOnServerSynchronously(key);
            if(!result.shouldCache()) {
                loadCallback.disableCaching();
            }

            loadCallback.synchronousSuccess(result.getModel(), result.getResponse());
            return result.getModel();
        } else {
            loadOnServerAsynchronously(key, loadCallback);
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

    protected void loadOnServerAsynchronously(Object key, LoadCallback callback) {
        throw new RuntimeException("loadOnServerAsynchronously() must be implemented before calling load with settings set to asynchronous");
    }

    protected Result loadOnServerSynchronously(Object key) {
        throw new RuntimeException("loadOnServerAsynchronously() must be implemented before calling load with settings set to synchronous");
    }

    protected SingleRecordSettings setupLoadSettings(SingleRecordSettings recordCallbackSettings, Object key) {
        throw new RuntimeException("setupLoadSettings() must be implemented before calling load");
    }

    public void delete(T model){
        SingleRecordSettings settings = setupDeleteSettings(new SingleRecordSettings(), model);
        final DeleteCallback deleteCallback = DeleteCallback.createFromSettings(settings, bus, newRelicManager);
        deleteOnServer(model, deleteCallback);
    }

    protected void deleteOnServer(T model, DeleteCallback deleteCallback) {
        throw new RuntimeException("deleteOnServer() must be implemented before calling delete");
    }

    protected SingleRecordSettings setupDeleteSettings(SingleRecordSettings recordCallbackSettings, T model) {
        throw new RuntimeException("setupDeleteSettings() must be implemented before calling delete");
    }
}
