package com.fanpics.opensource.android.modelrecord.callback;

import com.fanpics.opensource.android.modelrecord.HttpReport;
import com.fanpics.opensource.android.modelrecord.settings.SingleRecordSettings;
import com.squareup.otto.Bus;

public class UpdateCallback<T> extends RecordCallback<T> {

    protected UpdateCallback(SingleRecordSettings settings, Bus bus, HttpReport httpReport) {
        super(settings, bus, httpReport);
    }

    public static <T> UpdateCallback<T> createFromSettings(SingleRecordSettings<T> settings, Bus bus, HttpReport httpReport){
        return new UpdateCallback<T>(settings, bus, httpReport);
    }
}
