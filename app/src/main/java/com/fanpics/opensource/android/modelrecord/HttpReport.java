package com.fanpics.opensource.android.modelrecord;

import retrofit.RetrofitError;
import retrofit.client.Response;

public interface HttpReport {
    /**
     * Called on all successful network requests
     *
     * @param url Url from request
     * @param status Request status code
     * @param requestStartTime Time when request started
     * @param requestEndTime Time when request ended
     * @param response Actual Retrofit response object
     */
    public abstract void reportHttpSuccess(String url, int status, long requestStartTime,
                                  long requestEndTime, Response response);

    /**
     * Called on all failed network requests
     *
     *
     * @param url Url from request
     * @param requestStartTime Time when request started
     * @param requestEndTime Time when request ended
     * @param cause Exception which caused the failure
     * @param error Actual Retrofit Error object
     */
    public abstract void reportHttpError(String url, long requestStartTime, long requestEndTime,
                                Exception cause, RetrofitError error);
}
