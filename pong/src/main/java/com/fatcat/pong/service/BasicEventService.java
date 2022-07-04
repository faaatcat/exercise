package com.fatcat.pong.service;

import com.fatcat.pong.disruptor.MyFile;

public interface BasicEventService {
    public void publish(MyFile myFile);
    public long eventCount();
}
