package com.fanpics.opensource.android.modelrecord;

import android.os.Handler;

import com.fanpics.opensource.android.modelrecord.callback.CreateCallback;
import com.fanpics.opensource.android.modelrecord.callback.DeleteCallback;
import com.fanpics.opensource.android.modelrecord.callback.LoadCallback;
import com.fanpics.opensource.android.modelrecord.callback.LoadListCallback;
import com.fanpics.opensource.android.modelrecord.callback.UpdateCallback;
import com.fanpics.opensource.android.modelrecord.configuration.MultiRecordConfiguration;
import com.fanpics.opensource.android.modelrecord.configuration.SingleRecordConfiguration;
import com.fanpics.opensource.android.modelrecord.event.FailureEvent;
import com.fanpics.opensource.android.modelrecord.event.SuccessEvent;
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
    private SingleRecordConfiguration singleRecordConfiguration;
    private MultiRecordConfiguration multiRecordConfiguration;

    @Before
    public void createModelRecord() {
        modelRecord = mock(ModelRecord.class);
        modelRecord.handler = new Handler();
    }

    @Before
    public void createConfiguration() {
        singleRecordConfiguration = mock(SingleRecordConfiguration.class);
        multiRecordConfiguration = mock(MultiRecordConfiguration.class);
        cache = mock(RecordCache.class);

        setupConfiguration(singleRecordConfiguration);
        setupConfiguration(multiRecordConfiguration);
    }

    private void setupConfiguration(SingleRecordConfiguration configuration) {
        when(configuration.getCache()).thenReturn(cache);
        when(configuration.getSuccessEvent()).thenReturn(mock(SuccessEvent.class));
        when(configuration.getFailureEvent()).thenReturn(mock(FailureEvent.class));
    }

    private void setupConfiguration(MultiRecordConfiguration configuration) {
        when(configuration.getCache()).thenReturn(cache);
        when(configuration.getSuccessEvent()).thenReturn(mock(SuccessEvent.class));
        when(configuration.getFailureEvent()).thenReturn(mock(FailureEvent.class));
    }

    @Test
    public void testCreate() {
        final Object object = new Object();
        doCallRealMethod().when(modelRecord).create(object);
        when(modelRecord.setupCreateConfiguration(any(SingleRecordConfiguration.class), eq(object))).thenReturn(singleRecordConfiguration);
        modelRecord.create(object);

        verify(modelRecord).setupCreateConfiguration(any(SingleRecordConfiguration.class), eq(object));
        verify(singleRecordConfiguration).performAsynchronousNetworkCall(eq(object), any(CreateCallback.class));
    }

    @Test
    public void testUpdate() {
        final Object object = new Object();
        when(modelRecord.setupUpdateConfiguration(any(SingleRecordConfiguration.class), eq(object))).thenReturn(singleRecordConfiguration);
        doCallRealMethod().when(modelRecord).update(object);
        modelRecord.update(object);

        verify(modelRecord).setupUpdateConfiguration(any(SingleRecordConfiguration.class), eq(object));
        verify(singleRecordConfiguration).performAsynchronousNetworkCall(eq(object), any(UpdateCallback.class));
    }

    @Test
    public void testDelete() {
        final Object object = new Object();
        when(modelRecord.setupDeleteConfiguration(any(SingleRecordConfiguration.class), eq(object))).thenReturn(singleRecordConfiguration);
        doCallRealMethod().when(modelRecord).delete(object);
        modelRecord.delete(object);

        verify(modelRecord).setupDeleteConfiguration(any(SingleRecordConfiguration.class), eq(object));
        verify(singleRecordConfiguration).performAsynchronousNetworkCall(eq(object), any(DeleteCallback.class));
    }

    @Test
    public void testLoad() {
        modelRecord = mock(ModelRecord.class);
        doCallRealMethod().when(modelRecord).load();

        modelRecord.load();
        verify(modelRecord).load(null);
    }

    @Test
    public void testLoadingWithNetworkAndNoCache() {
        final Object object = new Object();

        when(modelRecord.setupLoadConfiguration(any(SingleRecordConfiguration.class), any(Object.class))).thenReturn(singleRecordConfiguration);

        when(singleRecordConfiguration.shouldLoadFromNetwork()).thenReturn(true);
        when(singleRecordConfiguration.shouldLoadFromCache()).thenReturn(false);

        doCallRealMethod().when(modelRecord).load(any(Object.class));
        doCallRealMethod().when(modelRecord).load(any(Object.class), any(SingleRecordConfiguration.class));
        modelRecord.load(object, new SingleRecordConfiguration(SingleRecordConfiguration.Type.LOAD));

        assertThat(singleRecordConfiguration.getSuccessEvent().hasFinished());
        verify(modelRecord).setupLoadConfiguration(any(SingleRecordConfiguration.class), eq(object));
        verify(singleRecordConfiguration).performAsynchronousNetworkCall(eq(object), any(LoadCallback.class));
        verify(cache, never()).load(eq(object));
    }

    @Test
    public void testLoadingWithNetworkAndCache() {
        final Object object = new Object();
        final Object result = new Object();

        when(modelRecord.setupLoadConfiguration(any(SingleRecordConfiguration.class), any(Object.class))).thenReturn(singleRecordConfiguration);
        when(singleRecordConfiguration.getCache()).thenReturn(cache);
        when(cache.load(object)).thenReturn(result);
        modelRecord.bus = mock(Bus.class);

        when(singleRecordConfiguration.shouldLoadFromNetwork()).thenReturn(true);
        when(singleRecordConfiguration.shouldLoadFromCache()).thenReturn(true);

        doCallRealMethod().when(modelRecord).load(any(Object.class));
        doCallRealMethod().when(modelRecord).load(any(Object.class), any(SingleRecordConfiguration.class));
        modelRecord.load(object, new SingleRecordConfiguration(SingleRecordConfiguration.Type.LOAD));

        assertThat(singleRecordConfiguration.getSuccessEvent().hasFinished());
        verify(cache).load(object);
        verify(modelRecord.bus).post(singleRecordConfiguration.getSuccessEvent());
        verify(singleRecordConfiguration).performAsynchronousNetworkCall(eq(object), any(LoadCallback.class));
    }

    @Test
    public void testLoadingWithCacheAndNoNetwork() {
        final Object object = new Object();
        final Object result = new Object();

        when(modelRecord.setupLoadConfiguration(any(SingleRecordConfiguration.class), any(Object.class))).thenReturn(singleRecordConfiguration);
        when(singleRecordConfiguration.getCache()).thenReturn(cache);
        when(cache.load(object)).thenReturn(result);
        modelRecord.bus = mock(Bus.class);

        when(singleRecordConfiguration.shouldLoadFromNetwork()).thenReturn(false);
        when(singleRecordConfiguration.shouldLoadFromCache()).thenReturn(true);

        doCallRealMethod().when(modelRecord).load(any(Object.class));
        doCallRealMethod().when(modelRecord).load(any(Object.class), any(SingleRecordConfiguration.class));
        modelRecord.load(object, new SingleRecordConfiguration(SingleRecordConfiguration.Type.LOAD));

        assertThat(singleRecordConfiguration.getSuccessEvent().hasFinished());
        verify(cache).load(object);
        verify(modelRecord.bus).post(singleRecordConfiguration.getSuccessEvent());
        verify(singleRecordConfiguration, never()).performAsynchronousNetworkCall(eq(object), any(LoadCallback.class));
    }

    @Test
    public void testLoadList() {
        modelRecord = mock(ModelRecord.class);
        doCallRealMethod().when(modelRecord).loadList();

        modelRecord.loadList();
        verify(modelRecord).loadList(null);
    }

    @Test
    public void testLoadingListWithNetworkAndNoCache() {
        final Object object = new Object();

        when(modelRecord.setupLoadListConfiguration(any(MultiRecordConfiguration.class), any(Object.class))).thenReturn(multiRecordConfiguration);
        when(multiRecordConfiguration.shouldLoadFromNetwork()).thenReturn(true);
        when(multiRecordConfiguration.shouldLoadFromCache()).thenReturn(false);

        doCallRealMethod().when(modelRecord).loadList(any(Object.class));
        doCallRealMethod().when(modelRecord).loadList(any(Object.class), any(MultiRecordConfiguration.class));
        modelRecord.loadList(object, new MultiRecordConfiguration(MultiRecordConfiguration.Type.LOAD));

        verify(modelRecord).setupLoadListConfiguration(any(MultiRecordConfiguration.class), any(Object.class));
        verify(multiRecordConfiguration).performAsynchronousNetworkCall(eq(object), any(LoadListCallback.class));
        verify(cache, never()).loadList(eq(object));
    }

    @Test
    public void testLoadingListWithCacheAndNetwork() {
        final Object object = new Object();
        final List<Object> result = new ArrayList<Object>();

        when(modelRecord.setupLoadListConfiguration(any(MultiRecordConfiguration.class), any(Object.class))).thenReturn(multiRecordConfiguration);
        when(cache.loadList(object)).thenReturn(result);
        modelRecord.bus = mock(Bus.class);

        when(multiRecordConfiguration.shouldLoadFromNetwork()).thenReturn(true);
        when(multiRecordConfiguration.shouldLoadFromCache()).thenReturn(true);

        doCallRealMethod().when(modelRecord).loadList(any(Object.class));
        doCallRealMethod().when(modelRecord).loadList(any(Object.class), any(MultiRecordConfiguration.class));
        modelRecord.loadList(object, new MultiRecordConfiguration(MultiRecordConfiguration.Type.LOAD));

        verify(cache).loadList(object);
        verify(modelRecord.bus).post(multiRecordConfiguration.getSuccessEvent());
        verify(multiRecordConfiguration).performAsynchronousNetworkCall(eq(object), any(LoadListCallback.class));
    }

    @Test
    public void testLoadingListWithCacheAndNoNetwork() {
        final Object object = new Object();
        final List<Object> result = new ArrayList<>();

        when(modelRecord.setupLoadListConfiguration(any(MultiRecordConfiguration.class), any(Object.class))).thenReturn(multiRecordConfiguration);
        when(cache.loadList(object)).thenReturn(result);
        modelRecord.bus = mock(Bus.class);

        when(multiRecordConfiguration.shouldLoadFromNetwork()).thenReturn(false);
        when(multiRecordConfiguration.shouldLoadFromCache()).thenReturn(true);

        doCallRealMethod().when(modelRecord).loadList(any(Object.class));
        doCallRealMethod().when(modelRecord).loadList(any(Object.class), any(MultiRecordConfiguration.class));
        modelRecord.loadList(object, new MultiRecordConfiguration(MultiRecordConfiguration.Type.LOAD));

        verify(cache).loadList(object);
        verify(modelRecord.bus).post(multiRecordConfiguration.getSuccessEvent());
        verify(multiRecordConfiguration, never()).performAsynchronousNetworkCall(eq(object), any(LoadListCallback.class));
    }

    @Test
    public void testRefresh() {
        modelRecord = mock(ModelRecord.class);
        doCallRealMethod().when(modelRecord).refresh();

        modelRecord.refresh();
        verify(modelRecord).refresh(null);
    }

    @Test
    public void testRefreshWithKey() {
        final Object key = new Object();
        ArgumentCaptor<SingleRecordConfiguration> settings = ArgumentCaptor.forClass(SingleRecordConfiguration.class);

        when(modelRecord.setupLoadConfiguration(any(SingleRecordConfiguration.class), any(Object.class))).thenReturn(this.singleRecordConfiguration);
        doCallRealMethod().when(modelRecord).refresh(any(Object.class));
        modelRecord.refresh(key);

        verify(modelRecord).loadAsynchronously(eq(key), settings.capture());
        assertThat(settings.getValue().getType()).isEqualTo(SingleRecordConfiguration.Type.REFRESH);
    }

    @Test
    public void testRefreshList() {
        modelRecord = mock(ModelRecord.class);
        doCallRealMethod().when(modelRecord).refreshList();

        modelRecord.refreshList();
        verify(modelRecord).refreshList(null);
    }

    @Test
    public void testRefreshListWithKey() {
        final Object key = new Object();
        ArgumentCaptor<MultiRecordConfiguration> settings = ArgumentCaptor.forClass(MultiRecordConfiguration.class);

        when(modelRecord.setupLoadListConfiguration(any(MultiRecordConfiguration.class), any(Object.class))).thenReturn(multiRecordConfiguration);
        doCallRealMethod().when(modelRecord).refreshList(any(Object.class));
        modelRecord.refreshList(key);

        verify(modelRecord).loadListAsynchronously(eq(key), settings.capture());
        assertThat(settings.getValue().getType()).isEqualTo(SingleRecordConfiguration.Type.REFRESH);
    }

    @Test
    public void testGetPreLoaded() {
        modelRecord = mock(ModelRecord.class);
        doCallRealMethod().when(modelRecord).getPreLoaded();

        modelRecord.getPreLoaded();
        verify(modelRecord).getPreLoaded(null);
    }

    @Test
    public void testGetPreLoadedWithKey() {
        final Object key = new Object();
        ArgumentCaptor<SingleRecordConfiguration> settings = ArgumentCaptor.forClass(SingleRecordConfiguration.class);

        when(modelRecord.setupLoadConfiguration(any(SingleRecordConfiguration.class), any(Object.class))).thenReturn(this.singleRecordConfiguration);
        doCallRealMethod().when(modelRecord).getPreLoaded(any(Object.class));
        modelRecord.getPreLoaded(key);

        verify(modelRecord).loadAsynchronously(eq(key), settings.capture());
        assertThat(settings.getValue().getType()).isEqualTo(SingleRecordConfiguration.Type.CACHE_ONLY);
    }

    @Test
    public void testGetPreLoadedList() {
        modelRecord = mock(ModelRecord.class);
        doCallRealMethod().when(modelRecord).getPreLoadedList();

        modelRecord.getPreLoadedList();
        verify(modelRecord).getPreLoadedList(null);
    }

    @Test
    public void testGetPreLoadedListWithKey() {
        final Object key = new Object();
        ArgumentCaptor<MultiRecordConfiguration> settings = ArgumentCaptor.forClass(MultiRecordConfiguration.class);

        when(modelRecord.setupLoadListConfiguration(any(MultiRecordConfiguration.class), any(Object.class))).thenReturn(multiRecordConfiguration);
        doCallRealMethod().when(modelRecord).getPreLoadedList(any(Object.class));
        modelRecord.getPreLoadedList(key);

        verify(modelRecord).loadListAsynchronously(eq(key), settings.capture());
        assertThat(settings.getValue().getType()).isEqualTo(SingleRecordConfiguration.Type.CACHE_ONLY);
    }

}
