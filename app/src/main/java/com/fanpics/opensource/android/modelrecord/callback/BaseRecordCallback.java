package com.fanpics.opensource.android.modelrecord.callback;

import android.os.Handler;

import com.fanpics.opensource.android.modelrecord.event.SuccessEvent;
import com.fanpics.opensource.android.modelrecord.settings.BaseRecordSettings;
import com.squareup.otto.Bus;

import java.util.Date;

import retrofit.RetrofitError;
import retrofit.client.Response;

public abstract class BaseRecordCallback {

    private final NewRelicManager newRelicManager;
    protected Bus bus;
    protected Handler handler;

    protected BaseRecordCallback(Bus bus, NewRelicManager newRelicManager){
        this.bus = bus;
        this.newRelicManager = newRelicManager;
    }

    public BaseRecordCallback(Bus bus, NewRelicManager newRelicManager, Handler handler) {
        this.bus = bus;
        this.newRelicManager = newRelicManager;
        this.handler = handler;
    }

    protected void sendHttpReport(Response response) {
        if (response != null) {
            final String url = response.getUrl();
            final int status = response.getStatus();
            final long startTime = getRecordCallbackSettings().getStartTime();
            final long currentTime = new Date().getTime();
            newRelicManager.reportHttpTransaction(url, status, startTime, currentTime);
        }
    }

    protected abstract BaseRecordSettings getRecordCallbackSettings();

    protected void postSuccessEvent(SuccessEvent event) {
        final Runnable successRunnable = createSuccessRunnable(event);

        if (handler != null){
            handler.post(successRunnable);
        } else {
            successRunnable.run();
        }
    }

    private Runnable createSuccessRunnable(final SuccessEvent event) {
        return new Runnable() {
            @Override
            public void run() {
                bus.post(event);
            }
        };
    }

    protected void postFailure(RetrofitError retrofitError) {
        if (retrofitError != null) {
            reportHttpFailure(retrofitError);
        }

        getRecordCallbackSettings().callFailureCallback();
        postFailureEvent();
    }

    private void reportHttpFailure(RetrofitError retrofitError) {
        final Throwable cause = retrofitError.getCause();
        final Response response = retrofitError.getResponse();
        if (retrofitError.isNetworkError()) {
            reportNetworkError(cause, retrofitError);
        } else if (response != null) {
            sendHttpReport(response);
        }
    }

    private void reportNetworkError(Throwable cause, RetrofitError error) {
        Exception e = null;
        if(cause instanceof Exception) {
            e = (Exception) cause;
        }

        final String url = error.getUrl();
        final long startTime = getRecordCallbackSettings().getStartTime();
        final long currentTime = new Date().getTime();
        newRelicManager.reportHttpError(url, startTime, currentTime, e);
    }

    private void postFailureEvent() {
        final Runnable failureRunnable = createFailureRunnable();

        if (handler != null){
            handler.post(failureRunnable);
        } else {
            failureRunnable.run();
        }
    }

    private Runnable createFailureRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                final Object event = getRecordCallbackSettings().getFailureEvent();
                bus.post(event);
            }
        };
    }

    public void disableCaching() {
        getRecordCallbackSettings().removeCache();
    }
}
