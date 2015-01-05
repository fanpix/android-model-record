package com.fanpics.opensource.android.modelrecord.callback;

import com.fanpics.opensource.android.modelrecord.RecordCache;
import com.fanpics.opensource.android.modelrecord.event.FailureEvent;
import com.fanpics.opensource.android.modelrecord.event.SuccessEvent;
import com.fanpics.opensource.android.modelrecord.configuration.SingleRecordConfiguration;
import com.squareup.otto.Bus;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeleteCallbackTest {

    private RecordCache cache;
    private SingleRecordConfiguration configuration;
    private Bus bus;

    @Before
    public void createRecordCallback() {
        configuration = mock(SingleRecordConfiguration.class);
        bus = mock(Bus.class);
        cache = mock(RecordCache.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFailureWithoutCache() {
        final Object model = new Object();
        DeleteCallback<Object> deleteCallback = DeleteCallback.createFromConfiguration(configuration, bus, null, model);
        when(configuration.getFailureEvent()).thenReturn(new FailureEvent());
        when(deleteCallback.configuration.getCache()).thenReturn(null);

        deleteCallback.failure(null);

        verify(cache, never()).delete(model);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSuccessWithCache() {
        final Object model = new Object();
        DeleteCallback<Object> deleteCallback = DeleteCallback.createFromConfiguration(configuration, bus, null, model);
        when(configuration.getSuccessEvent()).thenReturn(new SuccessEvent());
        when(deleteCallback.configuration.getCache()).thenReturn(cache);

        deleteCallback.success(new Object(), null);

        verify(cache).delete(model);
    }

}
