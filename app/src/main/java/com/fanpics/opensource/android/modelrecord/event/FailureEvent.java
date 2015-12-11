package com.fanpics.opensource.android.modelrecord.event;

import java.net.SocketTimeoutException;

import retrofit.RetrofitError;

public class FailureEvent {

    private RetrofitError error;

    public void setError(RetrofitError error) {
        this.error = error;
    }

    public RetrofitError getError() {
        return error;
    }

    public boolean isNetworkTimeoutError() {
        if (error.getCause() instanceof SocketTimeoutException){
            return true;
        }

        return false;
    }
}
