package com.fanpics.opensource.android.modelrecord.callback;

import com.fanpics.app.event.SuccessEvent;

import java.util.Date;

public abstract class BaseRecordSettings<T> {
    public static enum Type {REFRESH, CACHE_ONLY, NETWORK_AS_FALLBACK, LOAD;}

    private SuccessEvent<T> successEvent;
    private boolean runSynchronously;
    private com.fanpics.app.data.api.record.callback.SuccessCallback<T> successCallback;

    private Object failureEvent;
    private com.fanpics.app.data.api.record.callback.FailureCallback failureCallback;
    private final Date createdTime = new Date();
    private Type type;

    public BaseRecordSettings() {
    }

    public BaseRecordSettings(Type type) {
        this.type = type;
    }

    public boolean shouldLoadFromCache() {
        final boolean typeSupportsCacheLoading = type != Type.REFRESH;

        return typeSupportsCacheLoading && hasCache();
    }

    public boolean shouldLoadFromServer() {
        if (type != Type.NETWORK_AS_FALLBACK) {
            return (type == Type.LOAD) || (type == Type.REFRESH);
        } else {
            return !resultHasLoaded();
        }
    }

    public Type getType() {
        return type;
    }

    public void setSuccessEvent(SuccessEvent<T> successEvent) {
        this.successEvent = successEvent;
    }

    public SuccessEvent<T> getSuccessEvent() {
        return successEvent;
    }

    public void setSuccessCallback(com.fanpics.app.data.api.record.callback.SuccessCallback<T> successCallback) {
        this.successCallback = successCallback;
    }

    public void callSuccessCallback(T object) {
        if (successCallback != null) {
            successCallback.call(object);
        }
    }

    public void setFailureEvent(Object failureEvent) {
        this.failureEvent = failureEvent;
    }

    public Object getFailureEvent() {
        return failureEvent;
    }

    public void setFailureCallback(com.fanpics.app.data.api.record.callback.FailureCallback failureCallback) {
        this.failureCallback = failureCallback;
    }

    public void callFailureCallback() {
        if (failureCallback != null) {
            failureCallback.call();
        }
    }

    public long getStartTime() {
        return createdTime.getTime();
    }

    protected boolean resultHasLoaded() {
        return successEvent.resultHasLoaded();
    }

    public boolean shouldRunSynchronously() {
        return runSynchronously;
    }

    public void setRunAsynchronously() {
        runSynchronously = false;
    }

    public void setRunSynchronously() {
        runSynchronously = true;
    }

    public abstract void removeCache();

    protected abstract boolean hasCache();

}