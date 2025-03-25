package org.wqz.disruptordemo.Case;

import com.lmax.disruptor.RingBuffer;

class DataEventProducer {
    private final RingBuffer<DataEvent> ringBuffer;

    public DataEventProducer(RingBuffer<DataEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void onData(int data) {
        long sequence = ringBuffer.next();
        try {
            DataEvent event = ringBuffer.get(sequence);
            event.setData(data);
        } finally {
            ringBuffer.publish(sequence);
        }
    }
}