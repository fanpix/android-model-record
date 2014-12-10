package com.fanpics.opensource.android.modelrecord.callback;

import android.os.Handler;

import com.fanpics.opensource.android.modelrecord.HttpReport;
import com.fanpics.opensource.android.modelrecord.event.SuccessEvent;
import com.fanpics.opensource.android.modelrecord.settings.BaseRecordSettings;
import com.squareup.otto.Bus;

import java.util.Date;

import retrofit.RetrofitError;
import retrofit.client.Response;

public abstract class BaseRecordCallback {

    private final HttpReport httpReport;
    protected Bus bus;
    protected Handler handler;

    protected BaseRecordCallback(Bus bus, HttpReport httpReport){
        this.bus = bus;
        this.httpReport = httpReport;
    }

    public BaseRecordCallback(Bus bus, HttpReport httpReport, Handler handler) {
        this.bus = bus;
        this.httpReport = httpReport;
        this.handler = handler;
    }

    protected void sendHttpReport(Response response) {
        if (response != null && httpReport != null) {
            final String url = response.getUrl();
            final int status = response.getStatus();
            final long startTime = getRecordCallbackSettings().getStartTime();
            final long currentTime = new Date().getTime();
            httpReport.reportHttpSuccess(url, status, startTime, currentTime, response);
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
        Exception exception = null;
        if(cause instanceof Exception) {
            exception = (Exception) cause;
        }

        if (httpReport != null) {
            final String url = error.getUrl();
            final long startTime = getRecordCallbackSettings().getStartTime();
            final long currentTime = new Date().getTime();
            httpReport.reportHttpError(url, startTime, currentTime, exception, error);
        }
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