package com.fanpics.opensource.android.modelrecord.callback;

import com.fanpics.opensource.android.modelrecord.HttpReport;
import com.fanpics.opensource.android.modelrecord.settings.SingleRecordSettings;
import com.squareup.otto.Bus;

public class DeleteCallback<T> extends RecordCallback<T> {

    protected DeleteCallback(SingleRecordSettings settings, Bus bus, HttpReport httpReport) {
        super(settings, bus, httpReport);
    }

    public static <T> DeleteCallback<T> createFromSettings(SingleRecordSettings settings, Bus bus, HttpReport httpReport){
        return new DeleteCallback<T>(settings, bus, httpReport);
    }
}
