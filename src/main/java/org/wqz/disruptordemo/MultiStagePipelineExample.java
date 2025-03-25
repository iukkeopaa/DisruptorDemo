package org.wqz.disruptordemo;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// 定义事件类
class LogEvent {
    private String logMessage;

    public String getLogMessage() {
        return logMessage;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }
}

// 定义事件工厂
class LogEventFactory implements EventFactory<LogEvent> {
    @Override
    public LogEvent newInstance() {
        return new LogEvent();
    }
}

// 定义解析处理器
class LogParserHandler implements EventHandler<LogEvent> {
    @Override
    public void onEvent(LogEvent event, long sequence, boolean endOfBatch) throws Exception {
        System.out.println("解析日志: " + event.getLogMessage());
    }
}

// 定义过滤处理器
class LogFilterHandler implements EventHandler<LogEvent> {
    @Override
    public void onEvent(LogEvent event, long sequence, boolean endOfBatch) throws Exception {
        if (event.getLogMessage().contains("ERROR")) {
            System.out.println("过滤日志: " + event.getLogMessage());
        }
    }
}

// 定义存储处理器
class LogStorageHandler implements EventHandler<LogEvent> {
    @Override
    public void onEvent(LogEvent event, long sequence, boolean endOfBatch) throws Exception {
        if (event.getLogMessage().contains("ERROR")) {
            System.out.println("存储日志: " + event.getLogMessage());
        }
    }
}

// 定义生产者
class LogEventProducer {
    private final RingBuffer<LogEvent> ringBuffer;

    public LogEventProducer(RingBuffer<LogEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void onData(String logMessage) {
        long sequence = ringBuffer.next();
        try {
            LogEvent event = ringBuffer.get(sequence);
            event.setLogMessage(logMessage);
        } finally {
            ringBuffer.publish(sequence);
        }
    }
}

public class MultiStagePipelineExample {
    public static void main(String[] args) {
        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(4);

        // 创建事件工厂
        LogEventFactory factory = new LogEventFactory();

        // 定义环形缓冲区大小，必须是 2 的幂
        int bufferSize = 1024;

        // 创建 Disruptor
        Disruptor<LogEvent> disruptor = new Disruptor<>(factory, bufferSize, executor, ProducerType.SINGLE, new BlockingWaitStrategy());

        // 连接解析处理器
        EventHandlerGroup<LogEvent> parserGroup = disruptor.handleEventsWith(new LogParserHandler());

        // 连接过滤处理器，依赖于解析处理器
        EventHandlerGroup<LogEvent> filterGroup = parserGroup.then(new LogFilterHandler());

        // 连接存储处理器，依赖于过滤处理器
        filterGroup.then(new LogStorageHandler());

        // 启动 Disruptor
        RingBuffer<LogEvent> ringBuffer = disruptor.start();

        // 创建生产者
        LogEventProducer producer = new LogEventProducer(ringBuffer);

        // 模拟生产日志数据
        producer.onData("INFO: 系统正常运行");
        producer.onData("ERROR: 数据库连接失败");

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