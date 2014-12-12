package com.fanpics.opensource.android.modelrecord.settings;

import com.fanpics.opensource.android.modelrecord.RecordCache;
import com.fanpics.opensource.android.modelrecord.event.SuccessEvent;
import com.fanpics.opensource.android.modelrecord.settings.MultiRecordSettings;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class MultiRecordSettingsTest {

    private MultiRecordSettings multiRecordSettings;

    @Test
    public void testLoadTypeWithoutCache() {
        multiRecordSettings = new MultiRecordSettings(MultiRecordSettings.Type.LOAD);

        multiRecordSettings.setCache(null);
        assertThat(multiRecordSettings.shouldLoadFromCache()).isFalse();
    }

    @Test
    public void testLoadTypeWithCache() {
        multiRecordSettings = new MultiRecordSettings(MultiRecordSettings.Type.LOAD);

        multiRecordSettings.setCache(mock(RecordCache.class));
        assertThat(multiRecordSettings.shouldLoadFromCache()).isTrue();
        assertThat(multiRecordSettings.shouldLoadFromServer()).isTrue();
    }

    @Test
    public void testRefreshTypeWithoutCache() {
        multiRecordSettings = new MultiRecordSettings(MultiRecordSettings.Type.REFRESH);

        multiRecordSettings.setCache(null);
        assertThat(multiRecordSettings.shouldLoadFromCache()).isFalse();
    }

    @Test
    public void testRefreshTypeWithCache() {
        multiRecordSettings = new MultiRecordSettings(MultiRecordSettings.Type.REFRESH);

        multiRecordSettings.setCache(mock(RecordCache.class));
        assertThat(multiRecordSettings.shouldLoadFromCache()).isFalse();
        assertThat(multiRecordSettings.shouldLoadFromServer()).isTrue();
    }

    @Test
    public void testCacheOnlyTypeWithoutCache() {
        multiRecordSettings = new MultiRecordSettings(MultiRecordSettings.Type.CACHE_ONLY);

        multiRecordSettings.setCache(null);
        assertThat(multiRecordSettings.shouldLoadFromCache()).isFalse();
    }

    @Test
    public void testCacheOnlyTypeWithCache() {
        multiRecordSettings = new MultiRecordSettings(MultiRecordSettings.Type.CACHE_ONLY);

        multiRecordSettings.setCache(null);
        assertThat(multiRecordSettings.shouldLoadFromCache()).isFalse();
    }

    @Test
    public void testNetworkAsFallbackTypeWithoutCache() {
        multiRecordSettings = new MultiRecordSettings(MultiRecordSettings.Type.NETWORK_AS_FALLBACK);
        SuccessEvent event = new SuccessEvent();
        multiRecordSettings.setSuccessEvent(event);

        multiRecordSettings.setCache(null);
        assertThat(multiRecordSettings.shouldLoadFromCache()).isFalse();
    }

    @Test
    public void testNetworkAsFallbackTypeWithCacheButNoResult() {
        multiRecordSettings = new MultiRecordSettings(MultiRecordSettings.Type.NETWORK_AS_FALLBACK);
        SuccessEvent event = new SuccessEvent();
        multiRecordSettings.setSuccessEvent(event);

        multiRecordSettings.setCache(mock(RecordCache.class));
        assertThat(multiRecordSettings.shouldLoadFromCache()).isTrue();
        assertThat(multiRecordSettings.shouldLoadFromServer()).isTrue();
    }

    @Test
    public void testNetworkAsFallbackTypeWithCacheAndResult() {
        multiRecordSettings = new MultiRecordSettings(MultiRecordSettings.Type.NETWORK_AS_FALLBACK);
        SuccessEvent event = new SuccessEvent();
        multiRecordSettings.setSuccessEvent(event);

        event.setResult(new Object());
        multiRecordSettings.setCache(mock(RecordCache.class));
        assertThat(multiRecordSettings.shouldLoadFromCache()).isTrue();
        assertThat(multiRecordSettings.shouldLoadFromServer()).isFalse();
    }
}
