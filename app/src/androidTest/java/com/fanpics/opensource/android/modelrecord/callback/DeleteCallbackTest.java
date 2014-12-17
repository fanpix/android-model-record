package com.fanpics.opensource.android.modelrecord.callback;

import com.fanpics.opensource.android.modelrecord.RecordCache;
import com.fanpics.opensource.android.modelrecord.event.FailureEvent;
import com.fanpics.opensource.android.modelrecord.event.SuccessEvent;
import com.fanpics.opensource.android.modelrecord.settings.SingleRecordSettings;
import com.squareup.otto.Bus;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeleteCallbackTest {

    private RecordCache cache;
    private SingleRecordSettings settings;
    private Bus bus;

    @Before
    public void createRecordCallback() {
        settings = mock(SingleRecordSettings.class);
        bus = mock(Bus.class);
        cache = mock(RecordCache.class);
    }

    @Test
    public void testFailureWithoutCache() {
        final Object model = new Object();
        DeleteCallback<Object> deleteCallback = DeleteCallback.createFromSettings(settings, bus, null, model);
        when(settings.getFailureEvent()).thenReturn(new FailureEvent());
        when(deleteCallback.settings.getCache()).thenReturn(null);

        deleteCallback.failure(null);

        verify(cache, never()).delete(model);
    }

    @Test
    public void testSuccessWithCache() {
        final Object model = new Object();
        DeleteCallback<Object> deleteCallback = DeleteCallback.createFromSettings(settings, bus, null, model);
        when(settings.getSuccessEvent()).thenReturn(new SuccessEvent());
        when(deleteCallback.settings.getCache()).thenReturn(cache);

        deleteCallback.success(new Object(), null);

        verify(cache).delete(model);
    }

}
