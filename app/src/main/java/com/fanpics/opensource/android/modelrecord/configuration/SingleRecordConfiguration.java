package com.fanpics.opensource.android.modelrecord.configuration;

import com.fanpics.opensource.android.modelrecord.RecordCache;

public class SingleRecordConfiguration<T> extends BaseRecordConfiguration<T> {
    private RecordCache<T> cache;

    public SingleRecordConfiguration() {
    }

    public SingleRecordConfiguration(Type type) {
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
