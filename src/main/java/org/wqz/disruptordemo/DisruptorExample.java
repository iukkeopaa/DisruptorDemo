package org.wqz.disruptordemo;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// 定义事件类
class ValueEvent {
    private long value;

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}

// 定义事件工厂
class ValueEventFactory implements EventFactory<ValueEvent> {
    @Override
    public ValueEvent newInstance() {
        return new ValueEvent();
    }
}

// 定义事件处理程序（消费者）
class ValueEventHandler implements EventHandler<ValueEvent> {
    @Override
    public void onEvent(ValueEvent event, long sequence, boolean endOfBatch) throws Exception {
        System.out.println("消费者处理事件: " + event.getValue());
    }
}

// 定义生产者
class ValueEventProducer {
    private final RingBuffer<ValueEvent> ringBuffer;

    public ValueEventProducer(RingBuffer<ValueEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void onData(long value) {
        long sequence = ringBuffer.next();
        try {
            ValueEvent event = ringBuffer.get(sequence);
            event.setValue(value);
        } finally {
            ringBuffer.publish(sequence);
        }
    }
}

public class DisruptorExample {
    public static void main(String[] args) {
        // 创建线程池
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // 创建事件工厂
        ValueEventFactory factory = new ValueEventFactory();

        // 定义环形缓冲区大小，必须是 2 的幂
        int bufferSize = 1024;

        // 创建 Disruptor
        Disruptor<ValueEvent> disruptor = new Disruptor<>(factory, bufferSize, executor);

        // 连接处理程序
        disruptor.handleEventsWith(new ValueEventHandler());

        // 启动 Disruptor
        RingBuffer<ValueEvent> ringBuffer = disruptor.start();

        // 创建生产者
        ValueEventProducer producer = new ValueEventProducer(ringBuffer);

        // 生产数据
        for (long i = 0; i < 10; i++) {
            producer.onData(i);
        }

        // 关闭 Disruptor 和线程池
        disruptor.shutdown();
        executor.shutdown();
    }
}    