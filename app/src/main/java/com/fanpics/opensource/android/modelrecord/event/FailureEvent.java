package com.fanpics.opensource.android.modelrecord.event;

import retrofit.RetrofitError;

public class FailureEvent {

    private RetrofitError error;

    public void setError(RetrofitError error) {
        this.error = error;
    }

    public RetrofitError getError() {
        return error;
    }
}
