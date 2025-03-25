package org.wqz.disruptordemo.Case;

import com.lmax.disruptor.EventHandler;

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