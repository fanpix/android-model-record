package com.fanpics.opensource.android.modelrecord.configuration;

import com.fanpics.opensource.android.modelrecord.RecordCache;
import com.fanpics.opensource.android.modelrecord.event.SuccessEvent;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class SingleRecordConfigurationTest {

    private SingleRecordConfiguration singleRecordConfiguration;

    @Test
    public void testLoadTypeWithoutCache() {
        singleRecordConfiguration = new SingleRecordConfiguration(SingleRecordConfiguration.Type.LOAD);

        singleRecordConfiguration.setCache(null);
        assertThat(singleRecordConfiguration.shouldLoadFromCache()).isFalse();
    }

    @Test
    public void testLoadTypeWithCache() {
        singleRecordConfiguration = new SingleRecordConfiguration(SingleRecordConfiguration.Type.LOAD);

        singleRecordConfiguration.setCache(mock(RecordCache.class));
        assertThat(singleRecordConfiguration.shouldLoadFromCache()).isTrue();
        assertThat(singleRecordConfiguration.shouldLoadFromServer()).isTrue();
    }

    @Test
    public void testRefreshTypeWithoutCache() {
        singleRecordConfiguration = new SingleRecordConfiguration(SingleRecordConfiguration.Type.REFRESH);

        singleRecordConfiguration.setCache(null);
        assertThat(singleRecordConfiguration.shouldLoadFromCache()).isFalse();
    }

    @Test
    public void testRefreshTypeWithCache() {
        singleRecordConfiguration = new SingleRecordConfiguration(SingleRecordConfiguration.Type.REFRESH);

        singleRecordConfiguration.setCache(mock(RecordCache.class));
        assertThat(singleRecordConfiguration.shouldLoadFromCache()).isFalse();
        assertThat(singleRecordConfiguration.shouldLoadFromServer()).isTrue();
    }

    @Test
    public void testCacheOnlyTypeWithoutCache() {
        singleRecordConfiguration = new SingleRecordConfiguration(SingleRecordConfiguration.Type.CACHE_ONLY);

        singleRecordConfiguration.setCache(null);
        assertThat(singleRecordConfiguration.shouldLoadFromCache()).isFalse();
    }

    @Test
    public void testCacheOnlyTypeWithCache() {
        singleRecordConfiguration = new SingleRecordConfiguration(SingleRecordConfiguration.Type.CACHE_ONLY);

        singleRecordConfiguration.setCache(null);
        assertThat(singleRecordConfiguration.shouldLoadFromCache()).isFalse();
    }

    @Test
    public void testNetworkAsFallbackTypeWithoutCache() {
        singleRecordConfiguration = new SingleRecordConfiguration(SingleRecordConfiguration.Type.NETWORK_AS_FALLBACK);
        SuccessEvent event = new SuccessEvent();
        singleRecordConfiguration.setSuccessEvent(event);

        singleRecordConfiguration.setCache(null);
        assertThat(singleRecordConfiguration.shouldLoadFromCache()).isFalse();
    }

    @Test
    public void testNetworkAsFallbackTypeWithCacheButNoResult() {
        singleRecordConfiguration = new SingleRecordConfiguration(SingleRecordConfiguration.Type.NETWORK_AS_FALLBACK);
        SuccessEvent event = new SuccessEvent();
        singleRecordConfiguration.setSuccessEvent(event);

        singleRecordConfiguration.setCache(mock(RecordCache.class));
        assertThat(singleRecordConfiguration.shouldLoadFromCache()).isTrue();
        assertThat(singleRecordConfiguration.shouldLoadFromServer()).isTrue();
    }

    @Test
    public void testNetworkAsFallbackTypeWithCacheAndResult() {
        singleRecordConfiguration = new SingleRecordConfiguration(SingleRecordConfiguration.Type.NETWORK_AS_FALLBACK);
        SuccessEvent event = new SuccessEvent();
        singleRecordConfiguration.setSuccessEvent(event);

        event.setResult(new Object());
        singleRecordConfiguration.setCache(mock(RecordCache.class));
        assertThat(singleRecordConfiguration.shouldLoadFromCache()).isTrue();
        assertThat(singleRecordConfiguration.shouldLoadFromServer()).isFalse();
    }
}
