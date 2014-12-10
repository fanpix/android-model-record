package com.fanpics.opensource.android.modelrecord.callback;

import android.os.Handler;

import com.fanpics.opensource.android.modelrecord.settings.SingleRecordSettings;
import com.squareup.otto.Bus;

public class LoadCallback<T> extends RecordCallback<T> {

    protected LoadCallback(SingleRecordSettings settings, Bus bus, NewRelicManager newRelicManager, Object key, Handler handler) {
        super(settings, bus, newRelicManager, handler);
        setKey(key);
    }

    public static <T> LoadCallback<T> createFromSettings(SingleRecordSettings settings, Bus bus, NewRelicManager newRelicManager,
                                                         Object key, Handler handler){
        return new LoadCallback<T>(settings, bus, newRelicManager, key, handler);
    }

}
