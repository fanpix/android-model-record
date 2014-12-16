package com.fanpics.opensource.android.modelrecord;

import android.os.Handler;

import com.fanpics.opensource.android.modelrecord.callback.CreateCallback;
import com.fanpics.opensource.android.modelrecord.callback.DeleteCallback;
import com.fanpics.opensource.android.modelrecord.callback.LoadCallback;
import com.fanpics.opensource.android.modelrecord.callback.LoadListCallback;
import com.fanpics.opensource.android.modelrecord.callback.UpdateCallback;
import com.fanpics.opensource.android.modelrecord.event.FailureEvent;
import com.fanpics.opensource.android.modelrecord.event.SuccessEvent;
import com.fanpics.opensource.android.modelrecord.settings.MultiRecordSettings;
import com.fanpics.opensource.android.modelrecord.settings.SingleRecordSettings;
import com.squareup.otto.Bus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class ModelRecordTest {

    private ModelRecord modelRecord;
    private RecordCache cache;
    private SingleRecordSettings singleRecordSettings;
    private MultiRecordSettings multiRecordSettings;

    @Before
    public void createModelRecord() {
        modelRecord = mock(ModelRecord.class);
        modelRecord.handler = new Handler();
    }

    @Before
    public void createSettings() {
        singleRecordSettings = mock(SingleRecordSettings.class);
        multiRecordSettings = mock(MultiRecordSettings.class);
        cache = mock(RecordCache.class);

        setupSettings(singleRecordSettings);
        setupSettings(multiRecordSettings);
    }

    private void setupSettings(SingleRecordSettings settings) {
        when(settings.getCache()).thenReturn(cache);
        when(settings.getSuccessEvent()).thenReturn(mock(SuccessEvent.class));
        when(settings.getFailureEvent()).thenReturn(mock(FailureEvent.class));
    }

    private void setupSettings(MultiRecordSettings settings) {
        when(settings.getCache()).thenReturn(cache);
        when(settings.getSuccessEvent()).thenReturn(mock(SuccessEvent.class));
        when(settings.getFailureEvent()).thenReturn(mock(FailureEvent.class));
    }

    @Test
    public void testLoadingWithNetworkAndNoCache() {
        final Object object = new Object();

        when(modelRecord.setupLoadSettings(any(SingleRecordSettings.class), any(Object.class))).thenReturn(singleRecordSettings);

        when(singleRecordSettings.shouldLoadFromServer()).thenReturn(true);
        when(singleRecordSettings.shouldLoadFromCache()).thenReturn(false);

        doCallRealMethod().when(modelRecord).load(any(Object.class));
        doCallRealMethod().when(modelRecord).load(any(Object.class), any(SingleRecordSettings.class));
        modelRecord.load(object, new SingleRecordSettings(SingleRecordSettings.Type.LOAD));

        assertThat(singleRecordSettings.getSuccessEvent().hasFinished());
        verify(modelRecord).setupLoadSettings(any(SingleRecordSettings.class), eq(object));
        verify(singleRecordSettings).callOnServerAsync(eq(object), any(LoadCallback.class));
        verify(cache, never()).load(eq(object));
    }

    @Test
    public void testLoadingWithNetworkAndCache() {
        final Object object = new Object();
        final Object result = new Object();

        when(modelRecord.setupLoadSettings(any(SingleRecordSettings.class), any(Object.class))).thenReturn(singleRecordSettings);
        when(singleRecordSettings.getCache()).thenReturn(cache);
        when(cache.load(object)).thenReturn(result);
        modelRecord.bus = mock(Bus.class);

        when(singleRecordSettings.shouldLoadFromServer()).thenReturn(true);
        when(singleRecordSettings.shouldLoadFromCache()).thenReturn(true);

        doCallRealMethod().when(modelRecord).load(any(Object.class));
        doCallRealMethod().when(modelRecord).load(any(Object.class), any(SingleRecordSettings.class));
        modelRecord.load(object, new SingleRecordSettings(SingleRecordSettings.Type.LOAD));

        assertThat(singleRecordSettings.getSuccessEvent().hasFinished());
        verify(cache).load(object);
        verify(modelRecord.bus).post(singleRecordSettings.getSuccessEvent());
        verify(singleRecordSettings).callOnServerAsync(eq(object), any(LoadCallback.class));
    }

    @Test
    public void testLoadingWithCacheAndNoNetwork() {
        final Object object = new Object();
        final Object result = new Object();

        when(modelRecord.setupLoadSettings(any(SingleRecordSettings.class), any(Object.class))).thenReturn(singleRecordSettings);
        when(singleRecordSettings.getCache()).thenReturn(cache);
        when(cache.load(object)).thenReturn(result);
        modelRecord.bus = mock(Bus.class);

        when(singleRecordSettings.shouldLoadFromServer()).thenReturn(false);
        when(singleRecordSettings.shouldLoadFromCache()).thenReturn(true);

        doCallRealMethod().when(modelRecord).load(any(Object.class));
        doCallRealMethod().when(modelRecord).load(any(Object.class), any(SingleRecordSettings.class));
        modelRecord.load(object, new SingleRecordSettings(SingleRecordSettings.Type.LOAD));

        assertThat(singleRecordSettings.getSuccessEvent().hasFinished());
        verify(cache).load(object);
        verify(modelRecord.bus).post(singleRecordSettings.getSuccessEvent());
        verify(singleRecordSettings, never()).callOnServerAsync(eq(object), any(LoadCallback.class));
    }

    @Test
    public void testLoadingListWithNetworkAndNoCache() {
        final Object object = new Object();

        when(modelRecord.setupLoadListSettings(any(MultiRecordSettings.class), any(Object.class))).thenReturn(multiRecordSettings);
        when(multiRecordSettings.shouldLoadFromServer()).thenReturn(true);
        when(multiRecordSettings.shouldLoadFromCache()).thenReturn(false);

        doCallRealMethod().when(modelRecord).loadList(any(Object.class));
        doCallRealMethod().when(modelRecord).loadList(any(Object.class), any(MultiRecordSettings.class));
        modelRecord.loadList(object, new MultiRecordSettings(MultiRecordSettings.Type.LOAD));

        verify(modelRecord).setupLoadListSettings(any(MultiRecordSettings.class), any(Object.class));
        verify(multiRecordSettings).callOnServerAsync(eq(object), any(LoadListCallback.class));
        verify(cache, never()).loadList(eq(object));
    }

    @Test
    public void testLoadingListWithCacheAndNetwork() {
        final Object object = new Object();
        final List<Object> result = new ArrayList<Object>();

        when(modelRecord.setupLoadListSettings(any(MultiRecordSettings.class), any(Object.class))).thenReturn(multiRecordSettings);
        when(cache.loadList(object)).thenReturn(result);
        modelRecord.bus = mock(Bus.class);

        when(multiRecordSettings.shouldLoadFromServer()).thenReturn(true);
        when(multiRecordSettings.shouldLoadFromCache()).thenReturn(true);

        doCallRealMethod().when(modelRecord).loadList(any(Object.class));
        doCallRealMethod().when(modelRecord).loadList(any(Object.class), any(MultiRecordSettings.class));
        modelRecord.loadList(object, new MultiRecordSettings(MultiRecordSettings.Type.LOAD));

        verify(cache).loadList(object);
        verify(modelRecord.bus).post(multiRecordSettings.getSuccessEvent());
        verify(multiRecordSettings).callOnServerAsync(eq(object), any(LoadListCallback.class));
    }

    @Test
    public void testLoadingListWithCacheAndNoNetwork() {
        final Object object = new Object();
        final List<Object> result = new ArrayList<>();

        when(modelRecord.setupLoadListSettings(any(MultiRecordSettings.class), any(Object.class))).thenReturn(multiRecordSettings);
        when(cache.loadList(object)).thenReturn(result);
        modelRecord.bus = mock(Bus.class);

        when(multiRecordSettings.shouldLoadFromServer()).thenReturn(false);
        when(multiRecordSettings.shouldLoadFromCache()).thenReturn(true);

        doCallRealMethod().when(modelRecord).loadList(any(Object.class));
        doCallRealMethod().when(modelRecord).loadList(any(Object.class), any(MultiRecordSettings.class));
        modelRecord.loadList(object, new MultiRecordSettings(MultiRecordSettings.Type.LOAD));

        verify(cache).loadList(object);
        verify(modelRecord.bus).post(multiRecordSettings.getSuccessEvent());
        verify(multiRecordSettings, never()).callOnServerAsync(eq(object), any(LoadListCallback.class));
    }

    @Test
    public void testRefresh() {
        final Object key = new Object();
        ArgumentCaptor<SingleRecordSettings> settings = ArgumentCaptor.forClass(SingleRecordSettings.class);

        when(modelRecord.setupLoadSettings(any(SingleRecordSettings.class), any(Object.class))).thenReturn(this.singleRecordSettings);
        doCallRealMethod().when(modelRecord).refresh(any(Object.class));
        modelRecord.refresh(key);

        verify(modelRecord).loadAsynchronously(eq(key), settings.capture());
        assertThat(settings.getValue().getType()).isEqualTo(SingleRecordSettings.Type.REFRESH);
    }

    @Test
    public void testRefreshList() {
        final Object key = new Object();
        ArgumentCaptor<MultiRecordSettings> settings = ArgumentCaptor.forClass(MultiRecordSettings.class);

        when(modelRecord.setupLoadListSettings(any(MultiRecordSettings.class), any(Object.class))).thenReturn(multiRecordSettings);
        doCallRealMethod().when(modelRecord).refreshList(any(Object.class));
        modelRecord.refreshList(key);

        verify(modelRecord).loadListAsynchronously(eq(key), settings.capture());
        assertThat(settings.getValue().getType()).isEqualTo(SingleRecordSettings.Type.REFRESH);
    }

    @Test
    public void testGetPreLoaded() {
        final Object key = new Object();
        ArgumentCaptor<SingleRecordSettings> settings = ArgumentCaptor.forClass(SingleRecordSettings.class);

        when(modelRecord.setupLoadSettings(any(SingleRecordSettings.class), any(Object.class))).thenReturn(this.singleRecordSettings);
        doCallRealMethod().when(modelRecord).getPreLoaded(any(Object.class));
        modelRecord.getPreLoaded(key);

        verify(modelRecord).loadAsynchronously(eq(key), settings.capture());
        assertThat(settings.getValue().getType()).isEqualTo(SingleRecordSettings.Type.CACHE_ONLY);
    }

    @Test
    public void testGetPreLoadedList() {
        final Object key = new Object();
        ArgumentCaptor<MultiRecordSettings> settings = ArgumentCaptor.forClass(MultiRecordSettings.class);

        when(modelRecord.setupLoadListSettings(any(MultiRecordSettings.class), any(Object.class))).thenReturn(multiRecordSettings);
        doCallRealMethod().when(modelRecord).getPreLoadedList(any(Object.class));
        modelRecord.getPreLoadedList(key);

        verify(modelRecord).loadListAsynchronously(eq(key), settings.capture());
        assertThat(settings.getValue().getType()).isEqualTo(SingleRecordSettings.Type.CACHE_ONLY);
    }

    @Test
    public void testCreate() {
        final Object object = new Object();
        doCallRealMethod().when(modelRecord).create(object);
        when(modelRecord.setupCreateSettings(any(SingleRecordSettings.class), eq(object))).thenReturn(singleRecordSettings);
        modelRecord.create(object);

        verify(modelRecord).setupCreateSettings(any(SingleRecordSettings.class), eq(object));
        verify(singleRecordSettings).callOnServerAsync(eq(object), any(CreateCallback.class));
    }

    @Test
    public void testDelete() {
        final Object object = new Object();
        when(modelRecord.setupDeleteSettings(any(SingleRecordSettings.class), eq(object))).thenReturn(singleRecordSettings);
        doCallRealMethod().when(modelRecord).delete(object);
        modelRecord.delete(object);

        verify(modelRecord).setupDeleteSettings(any(SingleRecordSettings.class), eq(object));
        verify(singleRecordSettings).callOnServerAsync(eq(object), any(DeleteCallback.class));
    }

    @Test
    public void testUpdate() {
        final Object object = new Object();
        when(modelRecord.setupUpdateSettings(any(SingleRecordSettings.class), eq(object))).thenReturn(singleRecordSettings);
        doCallRealMethod().when(modelRecord).update(object);
        modelRecord.update(object);

        verify(modelRecord).setupUpdateSettings(any(SingleRecordSettings.class), eq(object));
        verify(singleRecordSettings).callOnServerAsync(eq(object), any(UpdateCallback.class));
    }

}
