package com.fanpics.opensource.android.modelrecord;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class HttpReport {
    public void reportHttpSuccess(String url, int status, long requestStartTime,
                                  long requestEndTime, Response response) {

    }

    public void reportHttpError(String url, long requestStartTime, long requestCurrentTime,
                                Exception exception, RetrofitError error) {

    }
}
