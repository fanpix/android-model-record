package com.fanpics.opensource.android.modelrecord.callback;

import com.fanpics.app.ui.NewRelicManager;
import com.squareup.otto.Bus;

public class DeleteCallback<T> extends com.fanpics.app.data.api.record.callback.RecordCallback<T> {

    protected DeleteCallback(com.fanpics.app.data.api.record.callback.SingleRecordSettings settings, Bus bus, NewRelicManager newRelicManager) {
        super(settings, bus, newRelicManager);
    }

    public static <T> DeleteCallback<T> createFromSettings(com.fanpics.app.data.api.record.callback.SingleRecordSettings settings, Bus bus, NewRelicManager newRelicManager){
        return new DeleteCallback<T>(settings, bus, newRelicManager);
    }
}
