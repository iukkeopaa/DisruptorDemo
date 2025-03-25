package org.wqz.disruptordemo.SpringBootPlusDistruptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.ByteBuffer;

@RestController
public class OrderController {
    @Autowired
    private OrderEventProducer producer;

    @GetMapping("/createOrder")
    public String createOrder() {
        ByteBuffer bb = ByteBuffer.allocate(16);
        bb.putLong(0, System.currentTimeMillis());
        bb.putDouble(8, 100.0);
        producer.onData(bb);
        return "Order created successfully";
    }
}    