package com.fanpics.opensource.android.modelrecord.event;

import com.squareup.otto.Bus;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class OttoProcessorTest {

    private OttoProcessor ottoProcessor;
    private Bus bus;

    @Before
    public void setupEvent() {
        bus = mock(Bus.class);
        ottoProcessor = new OttoProcessor(bus);
    }

    @Test
    public void processPostsToBus() {
        final Object object = new Object();
        ottoProcessor.process(object);

        verify(bus).post(object);
    }

}
