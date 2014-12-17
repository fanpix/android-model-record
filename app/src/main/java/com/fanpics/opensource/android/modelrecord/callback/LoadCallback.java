package com.fanpics.opensource.android.modelrecord.callback;

import android.os.Handler;

import com.fanpics.opensource.android.modelrecord.HttpReport;
import com.fanpics.opensource.android.modelrecord.configuration.SingleRecordConfiguration;
import com.squareup.otto.Bus;

public class LoadCallback<T> extends RecordCallback<T> {

    protected LoadCallback(SingleRecordConfiguration settings, Bus bus, HttpReport httpReport, Object key, Handler handler) {
        super(settings, bus, httpReport, handler);
        setKey(key);
    }

    public static <T> LoadCallback<T> createFromSettings(SingleRecordConfiguration settings, Bus bus, HttpReport httpReport,
                                                         Object key, Handler handler){
        return new LoadCallback<T>(settings, bus, httpReport, key, handler);
    }

}
