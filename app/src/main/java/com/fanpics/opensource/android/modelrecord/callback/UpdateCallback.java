package com.fanpics.opensource.android.modelrecord.callback;

import com.fanpics.opensource.android.modelrecord.settings.SingleRecordSettings;
import com.squareup.otto.Bus;

public class UpdateCallback<T> extends RecordCallback<T> {

    protected UpdateCallback(SingleRecordSettings settings, Bus bus, NewRelicManager newRelicManager) {
        super(settings, bus, newRelicManager);
    }

    public static <T> UpdateCallback<T> createFromSettings(SingleRecordSettings<T> settings, Bus bus, NewRelicManager newRelicManager){
        return new UpdateCallback<T>(settings, bus, newRelicManager);
    }
}
