package com.fanpics.opensource.android.modelrecord.callback;

import com.fanpics.opensource.android.modelrecord.HttpReport;
import com.fanpics.opensource.android.modelrecord.RecordCache;
import com.fanpics.opensource.android.modelrecord.configuration.SingleRecordConfiguration;
import com.squareup.otto.Bus;

public class DeleteCallback<T> extends RecordCallback<T> {

    protected DeleteCallback(SingleRecordConfiguration settings, Bus bus, HttpReport httpReport, T model) {
        super(settings, bus, httpReport);
        setKey(model);
    }

    public static <T> DeleteCallback<T> createFromSettings(SingleRecordConfiguration settings, Bus bus, HttpReport httpReport,
                                                           T model){
        return new DeleteCallback<T>(settings, bus, httpReport, model);
    }

    protected void manageCacheIfExists(T model) {
        final RecordCache<T> cache = settings.getCache();
        if (cache != null) {
            final T modelToDelete = (T) getKey();
            cache.delete(modelToDelete);
        }
    }
}
