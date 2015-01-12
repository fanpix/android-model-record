package com.fanpics.opensource.android.modelrecord.callback;

import com.fanpics.opensource.android.modelrecord.HttpReport;
import com.fanpics.opensource.android.modelrecord.configuration.SingleRecordConfiguration;
import com.fanpics.opensource.android.modelrecord.event.EventProcessor;

public class CreateCallback<T> extends RecordCallback<T> {

    protected CreateCallback(SingleRecordConfiguration<T> configuration, EventProcessor eventProcessor, HttpReport httpReport) {
        super(configuration, eventProcessor, httpReport);
    }

    public static <T> CreateCallback<T> createFromConfiguration(SingleRecordConfiguration<T> configuration, EventProcessor eventProcessor, HttpReport httpReport){
        return new CreateCallback<>(configuration, eventProcessor, httpReport);
    }
}
