package org.wqz.disruptordemo.Case;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DisruptorExample {
    public static void main(String[] args) {
        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(4);

        // 创建事件工厂
        DataEventFactory factory = new DataEventFactory();

        // 定义环形缓冲区大小，必须是 2 的幂
        int bufferSize = 1024;

        // 创建 Disruptor
        Disruptor<DataEvent> disruptor = new Disruptor<>(factory, bufferSize, executor, ProducerType.SINGLE, new BlockingWaitStrategy());

        // 连接事件处理程序
        disruptor.handleEventsWith(new DataEventHandler("消费者1"));

        // 启动 Disruptor
        RingBuffer<DataEvent> ringBuffer = disruptor.start();

        // 创建生产者
        DataEventProducer producer = new DataEventProducer(ringBuffer);

        // 模拟生产数据
        for (int i = 0; i < 10; i++) {
            producer.onData(i);
        }

        // 等待一段时间，确保所有事件都被处理
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