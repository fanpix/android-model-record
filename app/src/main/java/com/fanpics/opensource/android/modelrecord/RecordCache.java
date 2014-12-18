package com.fanpics.opensource.android.modelrecord;

import java.util.List;

public interface RecordCache<T> {

    public T load(Object key);

    public void store(Object key, T model);

    public void store(Object key, List<T> models);

    public List<T> loadList(Object object);

    public void clear();

    public void delete(T object);
}
