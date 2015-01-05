package com.fanpics.opensource.android.modelrecord.callback;

import com.fanpics.opensource.android.modelrecord.HttpReport;
import com.fanpics.opensource.android.modelrecord.configuration.SingleRecordConfiguration;
import com.squareup.otto.Bus;

public class CreateCallback<T> extends RecordCallback<T> {

    protected CreateCallback(SingleRecordConfiguration<T> configuration, Bus bus, HttpReport httpReport) {
        super(configuration, bus, httpReport);
    }

    public static <T> CreateCallback<T> createFromConfiguration(SingleRecordConfiguration<T> configuration, Bus bus, HttpReport httpReport){
        return new CreateCallback<>(configuration, bus, httpReport);
    }
}
