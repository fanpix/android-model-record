package com.fanpics.opensource.android.modelrecord.configuration;

import com.fanpics.opensource.android.modelrecord.Result;
import com.fanpics.opensource.android.modelrecord.callback.FailureCallback;
import com.fanpics.opensource.android.modelrecord.callback.SuccessCallback;
import com.fanpics.opensource.android.modelrecord.event.FailureEvent;
import com.fanpics.opensource.android.modelrecord.event.SuccessEvent;

import java.util.Date;

import retrofit.Callback;

public abstract class BaseRecordConfiguration<T> {
    private AsyncNetworkCall asyncNetworkCall;
    private SynchronousNetworkCall synchronousNetworkCall;

    public static enum Type {REFRESH, CACHE_ONLY, NETWORK_AS_FALLBACK, LOAD}

    private SuccessEvent<T> successEvent;
    private SuccessCallback<T> successCallback;
    private FailureEvent failureEvent;
    private FailureCallback failureCallback;
    private final Date createdTime = new Date();

    private boolean runSynchronously;
    private Type type;

    public BaseRecordConfiguration() {
    }

    public BaseRecordConfiguration(Type type) {
        this.type = type;
    }

    public boolean shouldLoadFromCache() {
        final boolean typeSupportsCacheLoading = type != Type.REFRESH;

        return typeSupportsCacheLoading && hasCache();
    }

    public boolean shouldLoadFromNetwork() {
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

    public void setSuccessCallback(SuccessCallback<T> successCallback) {
        this.successCallback = successCallback;
    }

    public void callSuccessCallback(T object) {
        if (successCallback != null) {
            successCallback.call(object);
        }
    }

    public void setFailureEvent(FailureEvent failureEvent) {
        this.failureEvent = failureEvent;
    }

    public FailureEvent getFailureEvent() {
        return failureEvent;
    }

    public void setFailureCallback(FailureCallback failureCallback) {
        this.failureCallback = failureCallback;
    }

    public void callFailureCallback() {
        if (failureCallback != null && failureEvent != null) {
            failureCallback.call(failureEvent);
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

    public boolean shouldRunAsynchronously() {
        return !runSynchronously;
    }

    public void setRunAsynchronously() {
        runSynchronously = false;
    }

    public void setRunSynchronously() {
        runSynchronously = true;
    }

    public void performAsynchronousNetworkCall(Object key, Callback callback) {
        if (asyncNetworkCall == null) {
            throw new RuntimeException("AsyncNetworkCall must be set with setAsynchronousNetworkCall(call) before performing an async action");
        }

        asyncNetworkCall.call(key, callback);
    }

    public Result performSynchronousNetworkCall(Object key) {
        if (synchronousNetworkCall == null) {
            throw new RuntimeException("SynchronousNetworkCall must be set with setSynchronousNetworkCall(call) before performing an async action");
        }

        return synchronousNetworkCall.call(key);
    }

    public void setAsynchronousNetworkCall(AsyncNetworkCall asyncNetworkCall) {
        this.asyncNetworkCall = asyncNetworkCall;
    }

    public void setSynchronousNetworkCall(SynchronousNetworkCall synchronousNetworkCall) {
        this.synchronousNetworkCall = synchronousNetworkCall;
    }

    public abstract void removeCache();

    protected abstract boolean hasCache();

    public interface AsyncNetworkCall {
        void call(Object key, Callback callback);
    }

    public interface SynchronousNetworkCall {
        Result call(Object key);
    }
}