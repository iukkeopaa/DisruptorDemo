package org.wqz.disruptordemo.SpringBootPlusDistruptor;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class DisruptorConfig {
    private static final int RING_BUFFER_SIZE = 1024;

    @Bean
    public Disruptor<OrderEvent> disruptor() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        OrderEventFactory factory = new OrderEventFactory();
        Disruptor<OrderEvent> disruptor = new Disruptor<>(factory, RING_BUFFER_SIZE, executor, ProducerType.SINGLE, new BlockingWaitStrategy());
        disruptor.handleEventsWith(new OrderEventHandler());
        disruptor.start();
        return disruptor;
    }

    @Bean
    public OrderEventProducer orderEventProducer(Disruptor<OrderEvent> disruptor) {
        return new OrderEventProducer(disruptor.getRingBuffer());
    }
}    