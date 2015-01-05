package com.fanpics.opensource.android.modelrecord.callback;

import android.os.Handler;

import com.fanpics.opensource.android.modelrecord.HttpReport;
import com.fanpics.opensource.android.modelrecord.configuration.SingleRecordConfiguration;
import com.squareup.otto.Bus;

public class LoadCallback<T> extends RecordCallback<T> {

    protected LoadCallback(SingleRecordConfiguration<T> configuration, Bus bus, HttpReport httpReport, Object key, Handler handler) {
        super(configuration, bus, httpReport, handler);
        setKey(key);
    }

    public static <T> LoadCallback<T> createFromConfiguration(SingleRecordConfiguration<T> configuration, Bus bus, HttpReport httpReport,
                                                              Object key, Handler handler){
        return new LoadCallback<>(configuration, bus, httpReport, key, handler);
    }

}
