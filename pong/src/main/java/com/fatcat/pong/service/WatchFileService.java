package com.fatcat.pong.service;

import com.fatcat.pong.disruptor.MyFile;

public interface WatchFileService {
    public void initWatch();
    public MyFile readFile(String fileName);
}
