package org.wqz.disruptordemo.Case;

import com.lmax.disruptor.EventFactory;

class DataEventFactory implements EventFactory<DataEvent> {
    @Override
    public DataEvent newInstance() {
        return new DataEvent();
    }
}