package org.wqz.disruptordemo.SpringBootPlusDistruptor;

import com.lmax.disruptor.RingBuffer;

import java.nio.ByteBuffer;

/**
 * @Description:
 * @Author: wjh
 * @Date: 2025/3/25 下午9:04
 */
public class OrderEventProducer {
    private final RingBuffer<OrderEvent> ringBuffer;

    public OrderEventProducer(RingBuffer<OrderEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void onData(ByteBuffer bb) {
        long sequence = ringBuffer.next();
        try {
            OrderEvent event = ringBuffer.get(sequence);
            event.setOrderId("Order-" + bb.getLong(0));
            event.setAmount(bb.getDouble(8));
        } finally {
            ringBuffer.publish(sequence);
        }
    }
}
