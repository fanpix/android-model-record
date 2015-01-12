package com.fanpics.opensource.android.modelrecord.callback;

import com.fanpics.opensource.android.modelrecord.HttpReport;
import com.fanpics.opensource.android.modelrecord.configuration.SingleRecordConfiguration;
import com.fanpics.opensource.android.modelrecord.event.EventProcessor;

public class UpdateCallback<T> extends RecordCallback<T> {

    protected UpdateCallback(SingleRecordConfiguration<T> configuration, EventProcessor eventProcessor, HttpReport httpReport) {
        super(configuration, eventProcessor, httpReport);
    }

    public static <T> UpdateCallback<T> createFromConfiguration(SingleRecordConfiguration<T> configuration, EventProcessor eventProcessor, HttpReport httpReport){
        return new UpdateCallback<>(configuration, eventProcessor, httpReport);
    }
}
