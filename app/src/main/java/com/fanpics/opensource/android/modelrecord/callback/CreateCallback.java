package com.fanpics.opensource.android.modelrecord.callback;

import com.fanpics.opensource.android.modelrecord.HttpReport;
import com.fanpics.opensource.android.modelrecord.settings.SingleRecordSettings;
import com.squareup.otto.Bus;

public class CreateCallback<T> extends RecordCallback<T> {

    protected CreateCallback(SingleRecordSettings settings, Bus bus, HttpReport httpReport) {
        super(settings, bus, httpReport);
    }

    public static <T> CreateCallback<T> createFromSettings(SingleRecordSettings settings, Bus bus, HttpReport httpReport){
        return new CreateCallback<T>(settings, bus, httpReport);
    }
}