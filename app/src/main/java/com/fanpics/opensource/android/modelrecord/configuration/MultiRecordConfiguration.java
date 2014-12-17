package com.fanpics.opensource.android.modelrecord.configuration;

import com.fanpics.opensource.android.modelrecord.RecordCache;

import java.util.List;

public class MultiRecordConfiguration<T> extends BaseRecordConfiguration<List<T>> {

    private RecordCache<T> cache;

    public MultiRecordConfiguration() {
    }

    public MultiRecordConfiguration(Type type) {
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
