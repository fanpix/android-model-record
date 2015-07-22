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
import com.fanpics.opensource.android.modelrecord.event.EventProcessor;
import com.fanpics.opensource.android.modelrecord.event.OttoProcessor;
import com.fanpics.opensource.android.modelrecord.event.SuccessEvent;
import com.squareup.otto.Bus;

import java.util.List;

public class ModelRecord<T> {
    protected EventProcessor eventProcessor;
    private HttpReport httpReport;
    protected Handler handler;

    /**
     *
     * @param bus Event bus to receive events
     */
    public ModelRecord(Bus bus) {
        this(bus, null);
    }

    /**
     *
     * @param bus Event bus to receive events
     * @param httpReport Class to receive metadata about http call failures and successes for
     *                   reporting purposes
     */
    public ModelRecord(Bus bus, HttpReport httpReport) {
        this(new OttoProcessor(bus), httpReport);
    }

    /**
     *
     * @param eventProcessor Event processor to receive events
     */
    public ModelRecord(EventProcessor eventProcessor) {
        this(eventProcessor, null);
    }

    /**
     *
     * @param eventProcessor Event processor to receive events
     * @param httpReport Class to receive metadata about http call failures and successes for
     *                   reporting purposes
     */
    public ModelRecord(EventProcessor eventProcessor, HttpReport httpReport) {
        this.eventProcessor = eventProcessor;
        this.httpReport = httpReport;
        this.handler = new Handler();
    }

    private void postOnMainThread(final Object event) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                eventProcessor.process(event);
            }
        });
    }

    /**
     * Creates record in cache/on server
     *
     * @param model Object to be persisted
     */
    public void create(T model){
        SingleRecordConfiguration configuration = setupCreateConfiguration(new SingleRecordConfiguration<T>(), model);
        final CreateCallback createCallback = CreateCallback.createFromConfiguration(configuration, eventProcessor, httpReport);
        configuration.performAsynchronousNetworkCall(model, createCallback);
    }

    /**
     * Prepares configuration for object creation.
     *
     * This must be overridden if create is going to be used
     *
     * @param configuration Base configuration to build upon. It can be ignored and a new one created if needed.
     * @param model Object passed in from #create(model)
     * @return Configuration to set up and place request
     */
    protected SingleRecordConfiguration setupCreateConfiguration(SingleRecordConfiguration configuration, T model) {
        throw new RuntimeException("SetupCreateConfiguration() must be implemented before calling create");
    }

    /**
     * Updates record in cache/on server
     *
     * @param model Object to be updated
     */
    public void update(T model){
        SingleRecordConfiguration configuration = setupUpdateConfiguration(new SingleRecordConfiguration<T>(), model);
        final UpdateCallback updateCallback = UpdateCallback.createFromConfiguration(configuration, eventProcessor, httpReport);
        configuration.performAsynchronousNetworkCall(model, updateCallback);
    }

    /**
     * Prepares configuration for object update.
     *
     * This must be overridden if update is going to be used
     *
     * @param configuration Base configuration to build upon. It can be ignored and a new one created if needed.
     * @param model Object passed in from #update(Object)
     * @return Configuration to set up and place request
     */
    protected SingleRecordConfiguration setupUpdateConfiguration(SingleRecordConfiguration configuration, T model) {
        throw new RuntimeException("setupUpdateConfiguration() must be implemented before calling update");
    }

    /**
     * Deletes record in cache/on server
     *
     * @param model Object to be deleted
     */
    public void delete(T model){
        SingleRecordConfiguration configuration = setupDeleteConfiguration(new SingleRecordConfiguration(), model);
        final DeleteCallback deleteCallback = DeleteCallback.createFromConfiguration(configuration, eventProcessor, httpReport, model);
        configuration.performAsynchronousNetworkCall(model, deleteCallback);
    }

    /**
     * Prepares configuration for object deletion.
     *
     * This must be overridden if delete is going to be used
     *
     * @param configuration Base configuration to build upon. It can be ignored and a new one created if needed.
     * @param model Object passed in from #delete(Object)
     * @return Configuration to set up and place request
     */
    protected SingleRecordConfiguration setupDeleteConfiguration(SingleRecordConfiguration configuration, T model) {
        throw new RuntimeException("setupDeleteConfiguration() must be implemented before calling delete");
    }

    /**
     * Calls #Load(Object) without a key
     */
    public void load() {
        load(null);
    }

    /**
     * Eager loads object from cache if present, then continues to load it from the network
     *
     * @param key Key to be sent in load call
     */
    public void load(Object key) {
        loadAsynchronously(key, new SingleRecordConfiguration<T>(SingleRecordConfiguration.Type.LOAD));
    }

    /**
     * Calls #refresh(Object) without a key
     */
    public void refresh() {
        refresh(null);
    }

    /**
     * Skips cache and loads object from the network
     *
     * @param key Key to be sent in load call
     */
    public void refresh(Object key) {
        loadAsynchronously(key, new SingleRecordConfiguration<T>(SingleRecordConfiguration.Type.REFRESH));
    }

    /**
     * Calls #getPreLoaded(Object) without a key
     */
    public void getPreLoaded() {
        getPreLoaded(null);
    }

    /**
     * Loads object from cache
     *
     * @param key Key to be sent in load call
     */
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

    /**
     * Calls #loadSynchronously(Object) without a key
     *
     * @return Loaded object
     */
    public Object loadSynchronously() {
        return loadSynchronously(null);
    }

    /**
     * Loads synchronously from cache, and falls back to network on cache miss
     *
     * @param key Key to be sent in load call
     * @return Loaded object
     */
    public Object loadSynchronously(Object key) {
        final SingleRecordConfiguration baseConfiguration = new SingleRecordConfiguration<T>(SingleRecordConfiguration.Type.LOAD);
        baseConfiguration.setRunSynchronously();
        return load(key, baseConfiguration);
    }

    /**
     * Calls @refreshSynchronously without a key
     *
     * @return Loaded object
     */
    public Object refreshSynchronously() {
        return refreshSynchronously(null);
    }

    /**
     * Loads object synchronously from network
     *
     * @param key Key to be sent in load call
     * @return Loaded object
     */
    public Object refreshSynchronously(Object key) {
        final SingleRecordConfiguration configuration = new SingleRecordConfiguration(SingleRecordConfiguration.Type.REFRESH);
        configuration.setRunSynchronously();
        return load(key, configuration);
    }

    /**
     * Calls @getPreLoadedSynchronously without a key
     *
     * @return Loaded object
     */
    public Object getPreLoadedSynchronously() {
        return getPreLoadedSynchronously(null);
    }

    /**
     * Loads object synchronously from cache
     *
     * @param key Key to be sent in load call
     * @return Loaded object
     */
    public Object getPreLoadedSynchronously(Object key) {
        final SingleRecordConfiguration configuration = new SingleRecordConfiguration(SingleRecordConfiguration.Type.CACHE_ONLY);
        configuration.setRunSynchronously();
        return load(key, configuration);
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
        if (configuration.shouldRunAsynchronously()) {
            postLoadedObject(object, configuration);
        }

        return object;
    }

    @SuppressWarnings("unchecked")
    private Object loadOnNetwork(Object key, SingleRecordConfiguration configuration) {
        final LoadCallback loadCallback = LoadCallback.createFromConfiguration(configuration, eventProcessor, httpReport, key, handler);
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

    /**
     * Prepares configuration for object loading.
     *
     * This must be overridden if any singular object load actions are going to be used
     *
     * @param configuration Base configuration to build upon. It can be ignored and a new one created if needed.
     * @param key Key passed in from load command
     * @return Configuration to set up and place request
     */
    protected SingleRecordConfiguration setupLoadConfiguration(SingleRecordConfiguration configuration, Object key) {
        throw new RuntimeException("setupLoadConfiguration() must be implemented before calling any load methods");
    }

    /**
     * Calls #loadList(Object) without a key
     */
    public void loadList() {
        loadList(null);
    }

    /**
     * Eager loads list from cache if present, then continues to load it from the network
     *
     * @param key Key to be sent in load call
     */
    public void loadList(Object key) {
        loadListAsynchronously(key, new MultiRecordConfiguration<T>(SingleRecordConfiguration.Type.LOAD));
    }

    /**
     * Calls #refreshList(Object) without a key
     */
    public void refreshList() {
        refreshList(null);
    }

    /**
     * Skips cache and loads list from the network
     *
     * @param key Key to be sent in load call
     */
    public void refreshList(Object key) {
        loadListAsynchronously(key, new MultiRecordConfiguration<T>(SingleRecordConfiguration.Type.REFRESH));
    }

    /**
     * Calls #getPreLoadedList(Object) without a key
     */
    public void getPreLoadedList() {
        getPreLoadedList(null);
    }

    /**
     * Loads list from cache
     *
     * @param key Key to be sent in load call
     */
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

    /**
     * Calls @loadListSynchronously without a key
     *
     * @return Loaded list
     */
    public List loadListSynchronously() {
        return loadListSynchronously(null);
    }

    /**
     * Loads list synchronously from cache, and falls back to network on cache miss
     *
     * @param key Key to be sent in load call
     * @return Loaded object
     */
    public List loadListSynchronously(Object key) {
        final MultiRecordConfiguration configuration = new MultiRecordConfiguration<T>(MultiRecordConfiguration.Type.LOAD);
        configuration.setRunSynchronously();
        return loadList(key, configuration);
    }

    /**
     * Calls @refreshListSynchronously without a key
     *
     * @return Loaded list
     */
    public List refreshListSynchronously() {
        return refreshListSynchronously(null);
    }

    /**
     * Loads list synchronously from network
     *
     * @param key Key to be sent in load call
     * @return Loaded object
     */
    public List refreshListSynchronously(Object key) {
        final MultiRecordConfiguration configuration = new MultiRecordConfiguration<T>(MultiRecordConfiguration.Type.REFRESH);
        configuration.setRunSynchronously();
        return loadList(key, configuration);
    }

    /**
     * Calls @getPreLoadedListSynchronously without a key
     *
     * @return Loaded list
     */
    public List getPreLoadedListSynchronously() {
        return getPreLoadedListSynchronously(null);
    }

    /**
     * Loads list synchronously from cache
     *
     * @param key Key to be sent in load call
     * @return Loaded object
     */
    public List getPreLoadedListSynchronously(Object key) {
        final MultiRecordConfiguration configuration = new MultiRecordConfiguration<T>(MultiRecordConfiguration.Type.CACHE_ONLY);
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
        if (configuration.shouldRunAsynchronously()) {
            postLoadedObject(object, configuration);
        }

        return object;
    }

    @SuppressWarnings("unchecked")
    private List loadListOnNetwork(Object key, MultiRecordConfiguration configuration) {
        final LoadListCallback loadListCallback = LoadListCallback.createFromConfiguration(configuration, eventProcessor, httpReport, handler);
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

    @SuppressWarnings("unchecked")
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

    /**
     * Prepares configuration for list loading.
     *
     * This must be overridden if any list load actions are going to be used
     *
     * @param configuration Base configuration to build upon. It can be ignored and a new one created if needed.
     * @param key Key passed in from load list command
     * @return Configuration to set up and place request
     */
    protected MultiRecordConfiguration setupLoadListConfiguration(MultiRecordConfiguration configuration, Object key) {
        throw new RuntimeException("setupLoadListConfiguration() must be implemented before calling any loadList methods");
    }
}
