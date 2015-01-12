package com.fanpics.opensource.android.modelrecord.event;

import com.squareup.otto.Bus;

public class OttoProcessor extends EventProcessor {
    private final Bus bus;

    public OttoProcessor(Bus bus) {
        this.bus = bus;
    }

    @Override
    public void process(Object object) {
        bus.post(object);
    }
}
