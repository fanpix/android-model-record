package com.fanpics.opensource.android.modelrecord.callback;

import android.os.Handler;

import com.fanpics.opensource.android.modelrecord.HttpReport;
import com.fanpics.opensource.android.modelrecord.event.EventProcessor;
import com.fanpics.opensource.android.modelrecord.event.FailureEvent;
import com.fanpics.opensource.android.modelrecord.event.SuccessEvent;
import com.fanpics.opensource.android.modelrecord.configuration.BaseRecordConfiguration;

import java.util.Date;

import retrofit.RetrofitError;
import retrofit.client.Response;

public abstract class BaseRecordCallback {

    private final HttpReport httpReport;
    protected EventProcessor eventProcessor;
    protected Handler handler;

    protected BaseRecordCallback(EventProcessor eventProcessor, HttpReport httpReport){
        this.eventProcessor = eventProcessor;
        this.httpReport = httpReport;
    }

    public BaseRecordCallback(EventProcessor eventProcessor, HttpReport httpReport, Handler handler) {
        this.eventProcessor = eventProcessor;
        this.httpReport = httpReport;
        this.handler = handler;
    }

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
                eventProcessor.process(event);
            }
        };
    }

    protected void postFailure(RetrofitError retrofitError) {
        if (retrofitError != null) {
            reportHttpFailure(retrofitError);
        }

        getRecordConfiguration().callFailureCallback();
        postFailureEvent(retrofitError);
    }

    private void reportHttpFailure(RetrofitError retrofitError) {
        final Throwable cause = retrofitError.getCause();
        final Response response = retrofitError.getResponse();
        final boolean isNetworkError = retrofitError.getKind() == RetrofitError.Kind.NETWORK;
        if (isNetworkError) {
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
            final long startTime = getRecordConfiguration().getStartTime();
            final long currentTime = new Date().getTime();
            httpReport.reportHttpError(url, startTime, currentTime, exception, error);
        }
    }

    protected void sendHttpReport(Response response) {
        if (response != null && httpReport != null) {
            final String url = response.getUrl();
            final int status = response.getStatus();
            final long startTime = getRecordConfiguration().getStartTime();
            final long currentTime = new Date().getTime();
            httpReport.reportHttpSuccess(url, status, startTime, currentTime, response);
        }
    }

    private void postFailureEvent(RetrofitError retrofitError) {
        final Runnable failureRunnable = createFailureRunnable(retrofitError);

        if (handler != null){
            handler.post(failureRunnable);
        } else {
            failureRunnable.run();
        }
    }

    private Runnable createFailureRunnable(final RetrofitError retrofitError) {
        return new Runnable() {
            @Override
            public void run() {
                final FailureEvent event = getRecordConfiguration().getFailureEvent();
                event.setError(retrofitError);
                eventProcessor.process(event);
            }
        };
    }

    public void disableCaching() {
        getRecordConfiguration().removeCache();
    }

    protected abstract BaseRecordConfiguration getRecordConfiguration();
}
