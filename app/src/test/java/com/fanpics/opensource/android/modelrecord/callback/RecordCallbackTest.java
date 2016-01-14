package com.fanpics.opensource.android.modelrecord.callback;

import com.fanpics.opensource.android.modelrecord.RecordCache;
import com.fanpics.opensource.android.modelrecord.configuration.SingleRecordConfiguration;
import com.fanpics.opensource.android.modelrecord.event.EventProcessor;
import com.fanpics.opensource.android.modelrecord.event.FailureEvent;
import com.fanpics.opensource.android.modelrecord.event.SuccessEvent;

import org.junit.Before;
import org.junit.Test;

import retrofit.RetrofitError;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RecordCallbackTest {

    private RecordCallback<Object> recordCallback;

    @Before
    @SuppressWarnings("unchecked")
    public void createRecordCallback() {
        recordCallback = mock(RecordCallback.class);
        recordCallback.configuration = mock(SingleRecordConfiguration.class);
        recordCallback.eventProcessor = mock(EventProcessor.class);
        RecordCache cache = mock(RecordCache.class);
        when(recordCallback.getRecordConfiguration()).thenCallRealMethod();
        when(recordCallback.configuration.getCache()).thenReturn(cache);
    }

    @Test
    public void testSuccessWithoutCache() {
        Object object = new Object();
        when(recordCallback.configuration.getCache()).thenReturn(null);
        SuccessEvent event = mock(SuccessEvent.class);

        when(recordCallback.configuration.getSuccessEvent()).thenReturn(event);

        doCallRealMethod().when(recordCallback).postSuccessEvent(any(SuccessEvent.class));
        doCallRealMethod().when(recordCallback).success(object, null);

        recordCallback.success(object, null);
        verify(recordCallback.configuration).callSuccessCallback(object);
        verify(recordCallback.eventProcessor).process(event);
    }

    @Test
    public void testSuccessWithCache() {
        Object object = new Object();
        SuccessEvent event = mock(SuccessEvent.class);

        when(recordCallback.configuration.getSuccessEvent()).thenReturn(event);

        doCallRealMethod().when(recordCallback).postSuccessEvent(any(SuccessEvent.class));
        doCallRealMethod().when(recordCallback).success(object, null);

        recordCallback.success(object, null);
        verify(recordCallback.configuration).callSuccessCallback(object);
        verify(recordCallback.eventProcessor).process(event);
        verify(recordCallback).runCacheThread(object);
    }

    @Test
    public void testSynchronousSuccessWithoutCache() {
        Object object = new Object();
        when(recordCallback.configuration.getCache()).thenReturn(null);
        SuccessEvent event = mock(SuccessEvent.class);

        when(recordCallback.configuration.getSuccessEvent()).thenReturn(event);

        doCallRealMethod().when(recordCallback).synchronousSuccess(object, null);

        recordCallback.synchronousSuccess(object, null);
        verify(recordCallback.configuration).callSuccessCallback(object);
        verify(recordCallback, never()).postSuccessEvent(event);
    }

    @Test
    public void testSynchronousSuccessWithCache() {
        Object object = new Object();
        SuccessEvent event = mock(SuccessEvent.class);

        when(recordCallback.configuration.getSuccessEvent()).thenReturn(event);

        doCallRealMethod().when(recordCallback).synchronousSuccess(object, null);

        recordCallback.synchronousSuccess(object, null);
        verify(recordCallback.configuration).callSuccessCallback(object);
        verify(recordCallback).runCacheThread(object);
        verify(recordCallback, never()).postSuccessEvent(event);
    }

    @Test
    public void testFailure() {
        final FailureEvent event = mock(FailureEvent.class);

        when(recordCallback.configuration.getFailureEvent()).thenReturn(event);
        doCallRealMethod().when(recordCallback).postFailure(any(RetrofitError.class));
        doCallRealMethod().when(recordCallback).failure(any(RetrofitError.class));

        recordCallback.failure(mock(RetrofitError.class));
        verify(recordCallback.configuration).callFailureCallback();
        verify(recordCallback.eventProcessor).process(event);
    }

}
