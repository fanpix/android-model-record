package com.fanpics.opensource.android.modelrecord.event;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

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
        if (error.getCause() instanceof SocketTimeoutException || error.getCause() instanceof UnknownHostException){
            return true;
        }

        return false;
    }
}
