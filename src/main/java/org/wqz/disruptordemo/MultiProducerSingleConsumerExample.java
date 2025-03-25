package org.wqz.disruptordemo;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// 定义事件类
class TradeEvent {
    private String symbol;
    private double price;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}

// 定义事件工厂
class TradeEventFactory implements EventFactory<TradeEvent> {
    @Override
    public TradeEvent newInstance() {
        return new TradeEvent();
    }
}

// 定义事件处理程序（消费者）
class TradeEventHandler implements EventHandler<TradeEvent> {
    @Override
    public void onEvent(TradeEvent event, long sequence, boolean endOfBatch) throws Exception {
        System.out.println("处理交易: " + event.getSymbol() + " 价格: " + event.getPrice());
    }
}

// 定义生产者
class TradeEventProducer {
    private final RingBuffer<TradeEvent> ringBuffer;

    public TradeEventProducer(RingBuffer<TradeEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void onData(String symbol, double price) {
        long sequence = ringBuffer.next();
        try {
            TradeEvent event = ringBuffer.get(sequence);
            event.setSymbol(symbol);
            event.setPrice(price);
        } finally {
            ringBuffer.publish(sequence);
        }
    }
}

public class MultiProducerSingleConsumerExample {
    public static void main(String[] args) {
        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(4);

        // 创建事件工厂
        TradeEventFactory factory = new TradeEventFactory();

        // 定义环形缓冲区大小，必须是 2 的幂
        int bufferSize = 1024;

        // 创建 Disruptor，指定为多生产者模式
        Disruptor<TradeEvent> disruptor = new Disruptor<>(factory, bufferSize, executor, ProducerType.MULTI, new BlockingWaitStrategy());

        // 连接处理程序
        disruptor.handleEventsWith(new TradeEventHandler());

        // 启动 Disruptor
        RingBuffer<TradeEvent> ringBuffer = disruptor.start();

        // 创建多个生产者
        TradeEventProducer producer1 = new TradeEventProducer(ringBuffer);
        TradeEventProducer producer2 = new TradeEventProducer(ringBuffer);

        // 模拟生产数据
        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                producer1.onData("AAPL", 100.0 + i);
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                producer2.onData("GOOG", 200.0 + i);
            }
        }).start();

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