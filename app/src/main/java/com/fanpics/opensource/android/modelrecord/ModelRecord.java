package com.fanpics.opensource.android.modelrecord;

import android.os.Handler;

import com.fanpics.opensource.android.modelrecord.callback.CreateCallback;
import com.fanpics.opensource.android.modelrecord.callback.DeleteCallback;
import com.fanpics.opensource.android.modelrecord.callback.LoadCallback;
import com.fanpics.opensource.android.modelrecord.callback.LoadListCallback;
import com.fanpics.opensource.android.modelrecord.callback.UpdateCallback;
import com.fanpics.opensource.android.modelrecord.configuration.BaseRecordConfiguration;
import com.fanpics.opensource.android.modelrecord.configuration.MultiRecordConfiguration;
import com.fanpics.opensource.android.modelrecord.configuration.SingleRecordConfiguration;
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
        SingleRecordConfiguration configuration = setupCreateSettings(new SingleRecordConfiguration<T>(), model);
        final CreateCallback createCallback = CreateCallback.createFromSettings(configuration, bus, httpReport);
        configuration.callOnServerAsync(model, createCallback);
    }

    protected SingleRecordConfiguration setupCreateSettings(SingleRecordConfiguration configuration, T model) {
        throw new RuntimeException("SetupCreateSettings() must be implemented before calling create");
    }

    public void update(T model){
        SingleRecordConfiguration configuration = setupUpdateSettings(new SingleRecordConfiguration<T>(), model);
        final UpdateCallback updateCallback = UpdateCallback.createFromSettings(configuration, bus, httpReport);
        configuration.callOnServerAsync(model, updateCallback);
    }

    public void delete(T model){
        SingleRecordConfiguration configuration = setupDeleteSettings(new SingleRecordConfiguration(), model);
        final DeleteCallback deleteCallback = DeleteCallback.createFromSettings(configuration, bus, httpReport, model);
        configuration.callOnServerAsync(model, deleteCallback);
    }

    protected SingleRecordConfiguration setupDeleteSettings(SingleRecordConfiguration configuration, T model) {
        throw new RuntimeException("setupDeleteSettings() must be implemented before calling delete");
    }

    protected SingleRecordConfiguration setupUpdateSettings(SingleRecordConfiguration configuration, T model) {
        throw new RuntimeException("setupUpdateSettings() must be implemented before calling update");
    }

    public void load() {
        load(null);
    }

    public void load(Object key) {
        loadAsynchronously(key, new SingleRecordConfiguration<T>(SingleRecordConfiguration.Type.LOAD));
    }

    public void refresh() {
        refresh(null);
    }

    public void refresh(Object key) {
        loadAsynchronously(key, new SingleRecordConfiguration<T>(SingleRecordConfiguration.Type.REFRESH));
    }

    public void getPreLoaded() {
        getPreLoaded(null);
    }

    public void getPreLoaded(Object key) {
        loadAsynchronously(key, new SingleRecordConfiguration<T>(SingleRecordConfiguration.Type.CACHE_ONLY));
    }

    protected void loadAsynchronously(final Object key, final SingleRecordConfiguration configuration){
        new Thread(new Runnable() {
            @Override
            public void run() {
                configuration.setRunAsynchronously();
                load(key, configuration);
            }
        }).start();
    }

    public Object loadSynchronously() {
        return loadSynchronously(null);
    }

    public Object loadSynchronously(final Object key) {
        final SingleRecordConfiguration configuration = new SingleRecordConfiguration<T>(SingleRecordConfiguration.Type.LOAD);
        configuration.setRunSynchronously();
        return load(key, configuration);
    }

    protected Object load(Object key, SingleRecordConfiguration configuration) {
        SingleRecordConfiguration settings = setupLoadSettings(configuration, key);

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

    private Object loadFromCache(Object key, SingleRecordConfiguration configuration) {
        final RecordCache cache = configuration.getCache();
        final Object object = cache.load(key);
        if (!configuration.shouldRunSynchronously()) {
            postLoadedObject(object, configuration);
        }

        return object;
    }

    private Object loadOnServer(Object key, SingleRecordConfiguration configuration) {
        final LoadCallback loadCallback = LoadCallback.createFromSettings(configuration, bus, httpReport, key, handler);
        if (configuration.shouldRunSynchronously()){
            final Result result = configuration.callOnServerSynchronously(key);
            if(!result.shouldCache()) {
                loadCallback.disableCaching();
            }

            loadCallback.synchronousSuccess(result.getModel(), result.getResponse());
            return result.getModel();
        } else {
            configuration.callOnServerAsync(key, loadCallback);
            return null;
        }
    }

    protected SingleRecordConfiguration setupLoadSettings(SingleRecordConfiguration configuration, Object key) {
        throw new RuntimeException("setupLoadSettings() must be implemented before calling load");
    }

    public void loadList() {
        loadList(null);
    }

    public void loadList(Object key) {
        loadListAsynchronously(key, new MultiRecordConfiguration<T>(SingleRecordConfiguration.Type.LOAD));
    }

    public void refreshList() {
        refreshList(null);
    }

    public void refreshList(Object key) {
        loadListAsynchronously(key, new MultiRecordConfiguration<T>(SingleRecordConfiguration.Type.REFRESH));
    }

    public void getPreLoadedList() {
        getPreLoadedList(null);
    }

    public void getPreLoadedList(Object key) {
        loadListAsynchronously(key, new MultiRecordConfiguration<T>(SingleRecordConfiguration.Type.CACHE_ONLY));
    }

    protected void loadListAsynchronously(final Object key, final MultiRecordConfiguration configuration){
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadList(key, configuration);
            }
        }).start();
    }

    public List loadListSynchronously() {
        return loadListSynchronously(null);
    }

    public List loadListSynchronously(final Object key) {
        final MultiRecordConfiguration configuration = new MultiRecordConfiguration<T>(MultiRecordConfiguration.Type.LOAD);
        configuration.setRunSynchronously();
        return loadList(key, configuration);
    }

    public List refreshListSynchronously(final Object key) {
        final MultiRecordConfiguration configuration = new MultiRecordConfiguration<T>(MultiRecordConfiguration.Type.REFRESH);
        configuration.setRunSynchronously();
        return loadList(key, configuration);
    }

    protected List loadList(Object key, MultiRecordConfiguration configuration) {
        MultiRecordConfiguration settings = setupLoadListSettings(configuration, key);
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

    private List loadListFromCache(Object key, MultiRecordConfiguration configuration) {
        final RecordCache cache = configuration.getCache();
        final List object = cache.loadList(key);
        if (!configuration.shouldRunSynchronously()) {
            postLoadedObject(object, configuration);
        }

        return object;
    }

    private List loadListOnServer(Object key, MultiRecordConfiguration configuration) {
        final LoadListCallback loadListCallback = LoadListCallback.createFromSettings(configuration, bus, httpReport, handler);
        if (configuration.shouldRunSynchronously()){
            final Result<List> result = configuration.callOnServerSynchronously(key);
            if(!result.shouldCache()) {
                loadListCallback.disableCaching();
            }

            loadListCallback.synchronousSuccess(result.getModel(), result.getResponse());
            return result.getModel();
        } else {
            configuration.callOnServerAsync(key, loadListCallback);
            return null;
        }
    }

    private void postLoadedObject(Object object, BaseRecordConfiguration configuration) {
        if (object != null) {
            final SuccessEvent event = configuration.getSuccessEvent();
            event.setHasFinished(!configuration.shouldLoadFromServer());
            event.setResult(object);
            postOnMainThread(event);
        } else if (!configuration.shouldLoadFromServer()) {
            final Object event = configuration.getFailureEvent();
            postOnMainThread(event);
        }
    }

    protected MultiRecordConfiguration setupLoadListSettings(MultiRecordConfiguration configuration, Object key) {
        throw new RuntimeException("setupLoadListSettings() must be implemented before calling loadList");
    }
}
