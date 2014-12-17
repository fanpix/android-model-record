package com.fanpics.opensource.android.modelrecord.configuration;

import com.fanpics.opensource.android.modelrecord.callback.FailureCallback;
import com.fanpics.opensource.android.modelrecord.callback.SuccessCallback;

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class BaseRecordConfigurationTest {

    private BaseRecordConfiguration configuration;

    @Before
    public void createRecordCallbackSettings() {
        configuration = new BaseRecordConfiguration() {
            @Override
            public void removeCache() {
            }

            @Override
            protected boolean hasCache() {
                return false;
            }
        };
    }

    @Test
    public void testCallSuccessCallbackDoesNOTCrashWhenEmpty() {
        configuration.setSuccessCallback(null);
        configuration.callSuccessCallback(null);
    }

    @Test
    public void testCallFailureCallbackDoesNOTCrashWhenEmpty() {
        configuration.setFailureCallback(null);
        configuration.callFailureCallback();
    }

    @Test
    public void testCallSuccessCallback() {
        BaseRecordConfiguration settings = mock(BaseRecordConfiguration.class);
        configuration.setSuccessCallback(new SuccessCallback<BaseRecordConfiguration>() {
            @Override
            public void call(BaseRecordConfiguration settings) {
                settings.getSuccessEvent();
            }
        });

        configuration.callSuccessCallback(settings);
        verify(settings).getSuccessEvent();
    }

    @Test
    public void testCallFailureCallback() {
        final BaseRecordConfiguration settings = mock(BaseRecordConfiguration.class);
        configuration.setFailureCallback(new FailureCallback() {
            @Override
            public void call() {
                settings.getFailureEvent();
            }
        });

        configuration.callFailureCallback();
        verify(settings).getFailureEvent();
    }

    @Test
    public void testSetRunSynchronously() {
        configuration.setRunSynchronously();

        assertThat(configuration.shouldRunSynchronously()).isTrue();
    }

    @Test
    public void testSetRunAsynchronously() {
        configuration.setRunAsynchronously();

        assertThat(configuration.shouldRunSynchronously()).isFalse();
    }

}
