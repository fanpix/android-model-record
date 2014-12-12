package com.fanpics.opensource.android.modelrecord.settings;

import com.fanpics.opensource.android.modelrecord.callback.FailureCallback;
import com.fanpics.opensource.android.modelrecord.callback.SuccessCallback;
import com.fanpics.opensource.android.modelrecord.settings.BaseRecordSettings;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class BaseRecordSettingsTest {

    private BaseRecordSettings recordCallbackSettings;

    @Before
    public void createRecordCallbackSettings() {
        recordCallbackSettings = new BaseRecordSettings() {
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
        recordCallbackSettings.setSuccessCallback(null);
        recordCallbackSettings.callSuccessCallback(null);
    }

    @Test
    public void testCallFailureCallbackDoesNOTCrashWhenEmpty() {
        recordCallbackSettings.setFailureCallback(null);
        recordCallbackSettings.callFailureCallback();
    }

    @Test
    public void testCallSuccessCallback() {
        BaseRecordSettings settings = mock(BaseRecordSettings.class);
        recordCallbackSettings.setSuccessCallback(new SuccessCallback<BaseRecordSettings>() {
            @Override
            public void call(BaseRecordSettings settings) {
                settings.getSuccessEvent();
            }
        });

        recordCallbackSettings.callSuccessCallback(settings);
        verify(settings).getSuccessEvent();
    }

    @Test
    public void testCallFailureCallback() {
        final BaseRecordSettings settings = mock(BaseRecordSettings.class);
        recordCallbackSettings.setFailureCallback(new FailureCallback() {
            @Override
            public void call() {
                settings.getFailureEvent();
            }
        });

        recordCallbackSettings.callFailureCallback();
        verify(settings).getFailureEvent();
    }

    @Test
    public void testSetRunSynchronously() {
        recordCallbackSettings.setRunSynchronously();

        assertThat(recordCallbackSettings.shouldRunSynchronously()).isTrue();
    }

    @Test
    public void testSetRunAsynchronously() {
        recordCallbackSettings.setRunAsynchronously();

        assertThat(recordCallbackSettings.shouldRunSynchronously()).isFalse();
    }

}
