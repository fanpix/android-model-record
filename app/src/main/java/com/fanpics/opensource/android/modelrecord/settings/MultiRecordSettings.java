package com.fanpics.opensource.android.modelrecord.settings;

import com.fanpics.opensource.android.modelrecord.RecordCache;

import java.util.List;

public class MultiRecordSettings<T> extends BaseRecordSettings<List<T>> {

    private RecordCache<T> cache;

    public MultiRecordSettings() {
    }

    public MultiRecordSettings(Type type) {
        super(type);
    }

    public void setCache(RecordCache<T> cache) {
        this.cache = cache;
    }

    public RecordCache<T> getCache() {
        return cache;
    }

    public void removeCache() {
        cache = null;
    }

    @Override
    protected boolean hasCache() {
        return cache != null;
    }

}
