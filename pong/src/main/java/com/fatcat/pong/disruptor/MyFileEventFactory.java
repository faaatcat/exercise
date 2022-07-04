package com.fatcat.pong.disruptor;

import com.lmax.disruptor.EventFactory;

public class MyFileEventFactory implements EventFactory<MyFileEvent> {
    @Override
    public MyFileEvent newInstance() {
        return new MyFileEvent();
    }
}
