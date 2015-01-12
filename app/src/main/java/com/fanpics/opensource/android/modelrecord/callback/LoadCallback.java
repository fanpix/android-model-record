package com.fanpics.opensource.android.modelrecord.callback;

import android.os.Handler;

import com.fanpics.opensource.android.modelrecord.HttpReport;
import com.fanpics.opensource.android.modelrecord.configuration.SingleRecordConfiguration;
import com.fanpics.opensource.android.modelrecord.event.EventProcessor;

public class LoadCallback<T> extends RecordCallback<T> {

    protected LoadCallback(SingleRecordConfiguration<T> configuration, EventProcessor eventProcessor, HttpReport httpReport, Object key, Handler handler) {
        super(configuration, eventProcessor, httpReport, handler);
        setKey(key);
    }

    public static <T> LoadCallback<T> createFromConfiguration(SingleRecordConfiguration<T> configuration, EventProcessor eventProcessor, HttpReport httpReport,
                                                              Object key, Handler handler){
        return new LoadCallback<>(configuration, eventProcessor, httpReport, key, handler);
    }

}
