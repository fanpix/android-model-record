package com.fanpics.opensource.android.modelrecord.configuration;

import com.fanpics.opensource.android.modelrecord.RecordCache;
import com.fanpics.opensource.android.modelrecord.event.SuccessEvent;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class MultiRecordConfigurationTest {

    private MultiRecordConfiguration multiRecordConfiguration;

    @Test
    public void testLoadTypeWithoutCache() {
        multiRecordConfiguration = new MultiRecordConfiguration(MultiRecordConfiguration.Type.LOAD);

        multiRecordConfiguration.setCache(null);
        assertThat(multiRecordConfiguration.shouldLoadFromCache()).isFalse();
    }

    @Test
    public void testLoadTypeWithCache() {
        multiRecordConfiguration = new MultiRecordConfiguration(MultiRecordConfiguration.Type.LOAD);

        multiRecordConfiguration.setCache(mock(RecordCache.class));
        assertThat(multiRecordConfiguration.shouldLoadFromCache()).isTrue();
        assertThat(multiRecordConfiguration.shouldLoadFromNetwork()).isTrue();
    }

    @Test
    public void testRefreshTypeWithoutCache() {
        multiRecordConfiguration = new MultiRecordConfiguration(MultiRecordConfiguration.Type.REFRESH);

        multiRecordConfiguration.setCache(null);
        assertThat(multiRecordConfiguration.shouldLoadFromCache()).isFalse();
    }

    @Test
    public void testRefreshTypeWithCache() {
        multiRecordConfiguration = new MultiRecordConfiguration(MultiRecordConfiguration.Type.REFRESH);

        multiRecordConfiguration.setCache(mock(RecordCache.class));
        assertThat(multiRecordConfiguration.shouldLoadFromCache()).isFalse();
        assertThat(multiRecordConfiguration.shouldLoadFromNetwork()).isTrue();
    }

    @Test
    public void testCacheOnlyTypeWithoutCache() {
        multiRecordConfiguration = new MultiRecordConfiguration(MultiRecordConfiguration.Type.CACHE_ONLY);

        multiRecordConfiguration.setCache(null);
        assertThat(multiRecordConfiguration.shouldLoadFromCache()).isFalse();
    }

    @Test
    public void testCacheOnlyTypeWithCache() {
        multiRecordConfiguration = new MultiRecordConfiguration(MultiRecordConfiguration.Type.CACHE_ONLY);

        multiRecordConfiguration.setCache(null);
        assertThat(multiRecordConfiguration.shouldLoadFromCache()).isFalse();
    }

    @Test
    public void testNetworkAsFallbackTypeWithoutCache() {
        multiRecordConfiguration = new MultiRecordConfiguration(MultiRecordConfiguration.Type.NETWORK_AS_FALLBACK);
        SuccessEvent event = new SuccessEvent();
        multiRecordConfiguration.setSuccessEvent(event);

        multiRecordConfiguration.setCache(null);
        assertThat(multiRecordConfiguration.shouldLoadFromCache()).isFalse();
    }

    @Test
    public void testNetworkAsFallbackTypeWithCacheButNoResult() {
        multiRecordConfiguration = new MultiRecordConfiguration(MultiRecordConfiguration.Type.NETWORK_AS_FALLBACK);
        SuccessEvent event = new SuccessEvent();
        multiRecordConfiguration.setSuccessEvent(event);

        multiRecordConfiguration.setCache(mock(RecordCache.class));
        assertThat(multiRecordConfiguration.shouldLoadFromCache()).isTrue();
        assertThat(multiRecordConfiguration.shouldLoadFromNetwork()).isTrue();
    }

    @Test
    public void testNetworkAsFallbackTypeWithCacheAndResult() {
        multiRecordConfiguration = new MultiRecordConfiguration(MultiRecordConfiguration.Type.NETWORK_AS_FALLBACK);
        SuccessEvent event = new SuccessEvent();
        multiRecordConfiguration.setSuccessEvent(event);

        event.setResult(new Object());
        multiRecordConfiguration.setCache(mock(RecordCache.class));
        assertThat(multiRecordConfiguration.shouldLoadFromCache()).isTrue();
        assertThat(multiRecordConfiguration.shouldLoadFromNetwork()).isFalse();
    }
}
