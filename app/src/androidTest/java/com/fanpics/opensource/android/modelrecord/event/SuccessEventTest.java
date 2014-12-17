package com.fanpics.opensource.android.modelrecord.event;

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class SuccessEventTest {

    private SuccessEvent<Object> successEvent;

    @Before
    public void setupEvent() {
        successEvent = new SuccessEvent<>();
    }

    @Test
    public void setResultShouldSetResult() {
        final Object result = new Object();
        successEvent.setResult(result);

        assertThat(successEvent.getResult()).isSameAs(result);
    }

    @Test
    public void setResultWithNullShouldClearOldValue() {
        final Object result = new Object();
        successEvent.setResult(result);
        successEvent.setResult(null);

        assertThat(successEvent.getResult()).isNull();
    }

    @Test
    public void clearResultShouldClearOldValue() {
        final Object result = new Object();
        successEvent.setResult(result);
        successEvent.clearResult();

        assertThat(successEvent.getResult()).isNull();
    }

    @Test
    public void resultHasLoadedShouldReturnFalseWithNewSuccessEvent() {
        successEvent = new SuccessEvent<>();

        assertThat(successEvent.resultHasLoaded()).isFalse();
    }

    @Test
    public void resultHasLoadedShouldReturnFalseAfterSetResultWithNull() {
        successEvent.setResult(null);

        assertThat(successEvent.resultHasLoaded()).isFalse();
    }

    @Test
    public void resultHasLoadedShouldReturnTrueAfterSetResult() {
        final Object result = new Object();
        successEvent.setResult(result);

        assertThat(successEvent.resultHasLoaded()).isTrue();
    }

    @Test
    public void resultHasLoadedShouldReturnTrueIfResultWasReplacedWithNull() {
        final Object result = new Object();
        successEvent.setResult(result);
        successEvent.setResult(null);

        assertThat(successEvent.resultHasLoaded()).isTrue();
    }

    @Test
    public void resultHasLoadedShouldReturnTrueIfClearResultWasCalled() {
        final Object result = new Object();
        successEvent.setResult(result);
        successEvent.clearResult();

        assertThat(successEvent.resultHasLoaded()).isTrue();
    }

}
