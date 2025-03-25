package org.wqz.disruptordemo;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// 定义事件类
class DataEvent {
    private int data;

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }
}

// 定义事件工厂
class DataEventFactory implements EventFactory<DataEvent> {
    @Override
    public DataEvent newInstance() {
        return new DataEvent();
    }
}

// 定义事件处理程序（消费者）
class DataEventHandler implements EventHandler<DataEvent> {
    private final String name;

    public DataEventHandler(String name) {
        this.name = name;
    }

    @Override
    public void onEvent(DataEvent event, long sequence, boolean endOfBatch) throws Exception {
        System.out.println(name + " 处理数据: " + event.getData());
    }
}

// 定义生产者
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

public class ParallelConsumersExample {
    public static void main(String[] args) {
        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(4);

        // 创建事件工厂
        DataEventFactory factory = new DataEventFactory();

        // 定义环形缓冲区大小，必须是 2 的幂
        int bufferSize = 1024;

        // 创建 Disruptor
        Disruptor<DataEvent> disruptor = new Disruptor<>(factory, bufferSize, executor, ProducerType.SINGLE, new BlockingWaitStrategy());

        // 连接多个消费者并行处理
        disruptor.handleEventsWith(new DataEventHandler("消费者1"), new DataEventHandler("消费者2"));

        // 启动 Disruptor
        RingBuffer<DataEvent> ringBuffer = disruptor.start();

        // 创建生产者
        DataEventProducer producer = new DataEventProducer(ringBuffer);

        // 模拟生产数据
        for (int i = 0; i < 10; i++) {
            producer.onData(i);
        }

        // 等待一段时间
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 关闭 Disruptor 和线程池
        disruptor.shutdown();
        executor.shutdown();
    }
}    