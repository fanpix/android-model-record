package com.fanpics.opensource.android.modelrecord.settings;

import com.fanpics.opensource.android.modelrecord.RecordCache;
import com.fanpics.opensource.android.modelrecord.event.SuccessEvent;
import com.fanpics.opensource.android.modelrecord.settings.SingleRecordSettings;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class SingleRecordSettingsTest {

    private SingleRecordSettings singleRecordSettings;

    @Test
    public void testLoadTypeWithoutCache() {
        singleRecordSettings = new SingleRecordSettings(SingleRecordSettings.Type.LOAD);

        singleRecordSettings.setCache(null);
        assertThat(singleRecordSettings.shouldLoadFromCache()).isFalse();
    }

    @Test
    public void testLoadTypeWithCache() {
        singleRecordSettings = new SingleRecordSettings(SingleRecordSettings.Type.LOAD);

        singleRecordSettings.setCache(mock(RecordCache.class));
        assertThat(singleRecordSettings.shouldLoadFromCache()).isTrue();
        assertThat(singleRecordSettings.shouldLoadFromServer()).isTrue();
    }

    @Test
    public void testRefreshTypeWithoutCache() {
        singleRecordSettings = new SingleRecordSettings(SingleRecordSettings.Type.REFRESH);

        singleRecordSettings.setCache(null);
        assertThat(singleRecordSettings.shouldLoadFromCache()).isFalse();
    }

    @Test
    public void testRefreshTypeWithCache() {
        singleRecordSettings = new SingleRecordSettings(SingleRecordSettings.Type.REFRESH);

        singleRecordSettings.setCache(mock(RecordCache.class));
        assertThat(singleRecordSettings.shouldLoadFromCache()).isFalse();
        assertThat(singleRecordSettings.shouldLoadFromServer()).isTrue();
    }

    @Test
    public void testCacheOnlyTypeWithoutCache() {
        singleRecordSettings = new SingleRecordSettings(SingleRecordSettings.Type.CACHE_ONLY);

        singleRecordSettings.setCache(null);
        assertThat(singleRecordSettings.shouldLoadFromCache()).isFalse();
    }

    @Test
    public void testCacheOnlyTypeWithCache() {
        singleRecordSettings = new SingleRecordSettings(SingleRecordSettings.Type.CACHE_ONLY);

        singleRecordSettings.setCache(null);
        assertThat(singleRecordSettings.shouldLoadFromCache()).isFalse();
    }

    @Test
    public void testNetworkAsFallbackTypeWithoutCache() {
        singleRecordSettings = new SingleRecordSettings(SingleRecordSettings.Type.NETWORK_AS_FALLBACK);
        SuccessEvent event = new SuccessEvent();
        singleRecordSettings.setSuccessEvent(event);

        singleRecordSettings.setCache(null);
        assertThat(singleRecordSettings.shouldLoadFromCache()).isFalse();
    }

    @Test
    public void testNetworkAsFallbackTypeWithCacheButNoResult() {
        singleRecordSettings = new SingleRecordSettings(SingleRecordSettings.Type.NETWORK_AS_FALLBACK);
        SuccessEvent event = new SuccessEvent();
        singleRecordSettings.setSuccessEvent(event);

        singleRecordSettings.setCache(mock(RecordCache.class));
        assertThat(singleRecordSettings.shouldLoadFromCache()).isTrue();
        assertThat(singleRecordSettings.shouldLoadFromServer()).isTrue();
    }

    @Test
    public void testNetworkAsFallbackTypeWithCacheAndResult() {
        singleRecordSettings = new SingleRecordSettings(SingleRecordSettings.Type.NETWORK_AS_FALLBACK);
        SuccessEvent event = new SuccessEvent();
        singleRecordSettings.setSuccessEvent(event);

        event.setResult(new Object());
        singleRecordSettings.setCache(mock(RecordCache.class));
        assertThat(singleRecordSettings.shouldLoadFromCache()).isTrue();
        assertThat(singleRecordSettings.shouldLoadFromServer()).isFalse();
    }
}
