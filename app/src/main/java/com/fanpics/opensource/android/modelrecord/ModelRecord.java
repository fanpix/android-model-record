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
        SingleRecordConfiguration configuration = setupCreateConfiguration(new SingleRecordConfiguration<T>(), model);
        final CreateCallback createCallback = CreateCallback.createFromConfiguration(configuration, bus, httpReport);
        configuration.performAsynchronousNetworkCall(model, createCallback);
    }

    protected SingleRecordConfiguration setupCreateConfiguration(SingleRecordConfiguration configuration, T model) {
        throw new RuntimeException("SetupCreateConfiguration() must be implemented before calling create");
    }

    public void update(T model){
        SingleRecordConfiguration configuration = setupUpdateConfiguration(new SingleRecordConfiguration<T>(), model);
        final UpdateCallback updateCallback = UpdateCallback.createFromConfiguration(configuration, bus, httpReport);
        configuration.performAsynchronousNetworkCall(model, updateCallback);
    }

    public void delete(T model){
        SingleRecordConfiguration configuration = setupDeleteConfiguration(new SingleRecordConfiguration(), model);
        final DeleteCallback deleteCallback = DeleteCallback.createFromConfiguration(configuration, bus, httpReport, model);
        configuration.performAsynchronousNetworkCall(model, deleteCallback);
    }

    protected SingleRecordConfiguration setupDeleteConfiguration(SingleRecordConfiguration configuration, T model) {
        throw new RuntimeException("setupDeleteConfiguration() must be implemented before calling delete");
    }

    protected SingleRecordConfiguration setupUpdateConfiguration(SingleRecordConfiguration configuration, T model) {
        throw new RuntimeException("setupUpdateConfiguration() must be implemented before calling update");
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

    protected void loadAsynchronously(final Object key, final SingleRecordConfiguration baseConfiguration){
        new Thread(new Runnable() {
            @Override
            public void run() {
                baseConfiguration.setRunAsynchronously();
                load(key, baseConfiguration);
            }
        }).start();
    }

    public Object loadSynchronously() {
        return loadSynchronously(null);
    }

    public Object loadSynchronously(final Object key) {
        final SingleRecordConfiguration baseConfiguration = new SingleRecordConfiguration<T>(SingleRecordConfiguration.Type.LOAD);
        baseConfiguration.setRunSynchronously();
        return load(key, baseConfiguration);
    }

    protected Object load(Object key, SingleRecordConfiguration baseConfiguration) {
        SingleRecordConfiguration configuration = setupLoadConfiguration(baseConfiguration, key);

        if (configuration.shouldLoadFromCache()) {
            final Object loadedObject = loadFromCache(key, configuration);
            if (loadedObject != null && configuration.shouldRunSynchronously()) {
                return loadedObject;
            }
        }

        if (configuration.shouldLoadFromNetwork()) {
            return loadOnNetwork(key, configuration);
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

    private Object loadOnNetwork(Object key, SingleRecordConfiguration configuration) {
        final LoadCallback loadCallback = LoadCallback.createFromConfiguration(configuration, bus, httpReport, key, handler);
        if (configuration.shouldRunSynchronously()){
            final Result result = configuration.performSynchronousNetworkCall(key);
            if(!result.shouldCache()) {
                loadCallback.disableCaching();
            }

            loadCallback.synchronousSuccess(result.getModel(), result.getResponse());
            return result.getModel();
        } else {
            configuration.performAsynchronousNetworkCall(key, loadCallback);
            return null;
        }
    }

    protected SingleRecordConfiguration setupLoadConfiguration(SingleRecordConfiguration configuration, Object key) {
        throw new RuntimeException("setupLoadConfiguration() must be implemented before calling load");
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

    protected List loadList(Object key, MultiRecordConfiguration baseConfiguration) {
        MultiRecordConfiguration configuration = setupLoadListConfiguration(baseConfiguration, key);
        if(configuration.shouldLoadFromCache()) {
            final List loadedObject = loadListFromCache(key, configuration);
            if (loadedObject != null && configuration.shouldRunSynchronously()) {
                return loadedObject;
            }
        }

        if(configuration.shouldLoadFromNetwork()) {
            return loadListOnNetwork(key, configuration);
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

    private List loadListOnNetwork(Object key, MultiRecordConfiguration configuration) {
        final LoadListCallback loadListCallback = LoadListCallback.createFromConfiguration(configuration, bus, httpReport, handler);
        if (configuration.shouldRunSynchronously()){
            final Result<List> result = configuration.performSynchronousNetworkCall(key);
            if(!result.shouldCache()) {
                loadListCallback.disableCaching();
            }

            loadListCallback.synchronousSuccess(result.getModel(), result.getResponse());
            return result.getModel();
        } else {
            configuration.performAsynchronousNetworkCall(key, loadListCallback);
            return null;
        }
    }

    private void postLoadedObject(Object object, BaseRecordConfiguration configuration) {
        if (object != null) {
            final SuccessEvent event = configuration.getSuccessEvent();
            event.setHasFinished(!configuration.shouldLoadFromNetwork());
            event.setResult(object);
            postOnMainThread(event);
        } else if (!configuration.shouldLoadFromNetwork()) {
            final Object event = configuration.getFailureEvent();
            postOnMainThread(event);
        }
    }

    protected MultiRecordConfiguration setupLoadListConfiguration(MultiRecordConfiguration configuration, Object key) {
        throw new RuntimeException("setupLoadListConfiguration() must be implemented before calling loadList");
    }
}
