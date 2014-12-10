package com.fanpics.opensource.android.modelrecord.callback;

import com.fanpics.app.ui.NewRelicManager;
import com.squareup.otto.Bus;

public class CreateCallback<T> extends com.fanpics.app.data.api.record.callback.RecordCallback<T> {

    protected CreateCallback(com.fanpics.app.data.api.record.callback.SingleRecordSettings settings, Bus bus, NewRelicManager newRelicManager) {
        super(settings, bus, newRelicManager);
    }

    public static <T> CreateCallback<T> createFromSettings(com.fanpics.app.data.api.record.callback.SingleRecordSettings settings, Bus bus, NewRelicManager newRelicManager){
        return new CreateCallback<T>(settings, bus, newRelicManager);
    }
}
