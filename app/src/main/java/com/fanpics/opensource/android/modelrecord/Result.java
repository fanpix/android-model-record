package com.fanpics.opensource.android.modelrecord;

import retrofit.client.Response;

public class Result<T> {

    final Response response;
    final T model;
    private boolean cacheable;

    public Result(Response response, T model) {
        this.response = response;
        this.model = model;
        this.cacheable = true;
    }

    public Response getResponse() {
        return response;
    }

    public T getModel() {
        return model;
    }

    public void disableCaching() {
        cacheable = false;
    }

    public boolean shouldCache() {
        return cacheable;
    }
}
