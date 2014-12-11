package com.fanpics.opensource.android.modelrecord;

import retrofit.RetrofitError;
import retrofit.client.Response;

public abstract class HttpReport {
    public abstract void reportHttpSuccess(String url, int status, long requestStartTime,
                                  long requestEndTime, Response response);

    public abstract void reportHttpError(String url, long requestStartTime, long requestCurrentTime,
                                Exception exception, RetrofitError error);
}
