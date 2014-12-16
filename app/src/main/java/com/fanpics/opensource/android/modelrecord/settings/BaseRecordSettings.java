package com.fanpics.opensource.android.modelrecord.settings;

import com.fanpics.opensource.android.modelrecord.Result;
import com.fanpics.opensource.android.modelrecord.callback.FailureCallback;
import com.fanpics.opensource.android.modelrecord.callback.SuccessCallback;
import com.fanpics.opensource.android.modelrecord.event.FailureEvent;
import com.fanpics.opensource.android.modelrecord.event.SuccessEvent;

import java.util.Date;

import retrofit.Callback;

public abstract class BaseRecordSettings<T> {
    private AsyncServerCall asyncServerCall;
    private SynchronousServerCall synchronousServerCall;

    public static enum Type {REFRESH, CACHE_ONLY, NETWORK_AS_FALLBACK, LOAD;}

    private SuccessEvent<T> successEvent;
    private boolean runSynchronously;

    private SuccessCallback<T> successCallback;

    private FailureEvent failureEvent;
    private FailureCallback failureCallback;
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

    public void callOnServerAsync(Object key, Callback callback) {
        if (asyncServerCall == null) {
            throw new RuntimeException("AsyncServerCall must be set with setAsyncServerCall(call) before performing an async action");
        }

        asyncServerCall.call(key, callback);
    }

    public Result callOnServerSynchronously(Object key) {
        if (synchronousServerCall == null) {
            throw new RuntimeException("SynchronousServerCall must be set with setSynchronousServerCall(call) before performing an async action");
        }

        return synchronousServerCall.call(key);
    }

    public void setAsyncServerCall(AsyncServerCall asyncServerCall) {
        this.asyncServerCall = asyncServerCall;
    }

    public void setSynchronousServerCall(SynchronousServerCall synchronousServerCall) {
        this.synchronousServerCall = synchronousServerCall;
    }

    public abstract void removeCache();

    protected abstract boolean hasCache();

    public interface AsyncServerCall {
        void call(Object key, Callback callback);
    }

    private interface SynchronousServerCall {
        Result call(Object key);
    }
}