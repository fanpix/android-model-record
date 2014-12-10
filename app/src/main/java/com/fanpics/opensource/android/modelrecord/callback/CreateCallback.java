package com.fanpics.opensource.android.modelrecord.callback;

import com.squareup.otto.Bus;

public class CreateCallback<T> extends RecordCallback<T> {

    protected CreateCallback(SingleRecordSettings settings, Bus bus, NewRelicManager newRelicManager) {
        super(settings, bus, newRelicManager);
    }

    public static <T> CreateCallback<T> createFromSettings(SingleRecordSettings settings, Bus bus, NewRelicManager newRelicManager){
        return new CreateCallback<T>(settings, bus, newRelicManager);
    }
}
