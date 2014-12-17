package com.fanpics.opensource.android.modelrecord.callback;

import com.fanpics.opensource.android.modelrecord.HttpReport;
import com.fanpics.opensource.android.modelrecord.configuration.SingleRecordConfiguration;
import com.squareup.otto.Bus;

public class UpdateCallback<T> extends RecordCallback<T> {

    protected UpdateCallback(SingleRecordConfiguration configuration, Bus bus, HttpReport httpReport) {
        super(configuration, bus, httpReport);
    }

    public static <T> UpdateCallback<T> createFromConfiguration(SingleRecordConfiguration<T> configuration, Bus bus, HttpReport httpReport){
        return new UpdateCallback<T>(configuration, bus, httpReport);
    }
}
