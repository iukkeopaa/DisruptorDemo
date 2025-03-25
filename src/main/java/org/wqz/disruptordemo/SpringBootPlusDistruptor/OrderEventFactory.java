package org.wqz.disruptordemo.SpringBootPlusDistruptor;

import com.lmax.disruptor.EventFactory;
import org.wqz.disruptordemo.SpringBootPlusDistruptor.OrderEvent;



public class OrderEventFactory implements EventFactory<OrderEvent> {
    @Override
    public OrderEvent newInstance() {
        return new OrderEvent();
    }
}    