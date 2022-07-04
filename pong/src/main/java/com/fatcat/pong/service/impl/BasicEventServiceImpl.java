package com.fatcat.pong.service.impl;

import com.fatcat.pong.disruptor.*;
import com.fatcat.pong.service.BasicEventService;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

@Service
@Slf4j
public class BasicEventServiceImpl implements BasicEventService {

    private static final int RING_BUFFER_SIZE = 1024;

    @Value("${pong.path}")
    private String path;

    private Disruptor<MyFileEvent> disruptor;

    private MyFileEventProducer producer;

    /**
     * 统计消息总数
     */
    private final AtomicLong eventCount = new AtomicLong();

    @PostConstruct
    public void init() {
// 创建缓冲池
//        ExecutorService executorService = Executors.newCachedThreadPool();
        // 创建工厂
        MyFileEventFactory factory = new MyFileEventFactory();

        // new Disruptor<>(new MyFileEventFactory(),ringBufferSize,executorService, ProducerType.SINGLE,new YieldingWaitStrategy());
        disruptor = new Disruptor<>(new MyFileEventFactory(), RING_BUFFER_SIZE, new CustomizableThreadFactory("event-handler-"), ProducerType.SINGLE, new YieldingWaitStrategy());

        // 这样每次处理事件时，都会将已经处理事件的总数打印出来
        Consumer<?> eventCountPrinter = new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                long count = eventCount.incrementAndGet();
                log.info("receive [{}] event", count);
            }
        };
        // 连接消费事件方法
        disruptor.handleEventsWith(new MyFileEventHandler(eventCountPrinter));

        // 启动
        disruptor.start();
        // 发布事件
        RingBuffer<MyFileEvent> ringBuffer = disruptor.getRingBuffer();
        producer = new MyFileEventProducer(ringBuffer);

//        producer.onData(new MyFile(String.valueOf(i), String.valueOf(i)));

//        disruptor.shutdown();
    }



    @Override
    public void publish(MyFile myFile) {
        producer.onData(myFile);
    }

    @Override
    public long eventCount() {
        return eventCount.get();
    }
}
