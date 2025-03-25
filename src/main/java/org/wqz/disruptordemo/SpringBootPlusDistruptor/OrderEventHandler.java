package org.wqz.disruptordemo.SpringBootPlusDistruptor;

import com.lmax.disruptor.EventHandler;

/**
 * @Description:
 * @Author: wjh
 * @Date: 2025/3/25 下午9:04
 */
public class OrderEventHandler implements EventHandler<OrderEvent> {
    @Override
    public void onEvent(OrderEvent event, long sequence, boolean endOfBatch) throws Exception {
        System.out.println("Processing order: " + event);
        // 这里可以添加更复杂的业务逻辑，如保存到数据库等
    }
}
