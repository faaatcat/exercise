package com.fatcat.pong.mytest;

import com.fatcat.pong.disruptor.*;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class MyFileMain {
    public static void main(String[] args) {
        // 创建缓冲池
//        ExecutorService executorService = Executors.newCachedThreadPool();
        // 创建工厂
        MyFileEventFactory factory = new MyFileEventFactory();
        int ringBufferSize = 1024;
        // new Disruptor<>(new MyFileEventFactory(),ringBufferSize,executorService, ProducerType.SINGLE,new YieldingWaitStrategy());
        Disruptor<MyFileEvent> disruptor = new Disruptor<>(new MyFileEventFactory(), ringBufferSize, new CustomizableThreadFactory("event-handler-"), ProducerType.SINGLE, new YieldingWaitStrategy());

        // 连接消费事件方法
        disruptor.handleEventsWith(new MyFileEventHandler());

        // 启动
        disruptor.start();
        // 发布事件
        RingBuffer<MyFileEvent> ringBuffer = disruptor.getRingBuffer();
        MyFileEventProducer producer = new MyFileEventProducer(ringBuffer);
        for(int i=0;i<100;i++){
            producer.onData(new MyFile(String.valueOf(i),String.valueOf(i)));
        }
        disruptor.shutdown();
    }




}
