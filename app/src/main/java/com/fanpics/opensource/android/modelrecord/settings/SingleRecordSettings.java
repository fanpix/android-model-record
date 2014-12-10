package com.fanpics.opensource.android.modelrecord.settings;

import com.fanpics.opensource.android.modelrecord.RecordCache;

public class SingleRecordSettings<T> extends BaseRecordSettings<T> {
    private RecordCache<T> cache;

    public SingleRecordSettings() {
    }

    public SingleRecordSettings(Type type) {
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
