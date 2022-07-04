package com.fatcat.pong.disruptor;

import com.lmax.disruptor.RingBuffer;

import java.nio.ByteBuffer;

/**
 * 消息的生产者
 */
public class MyFileEventProducer {
    private final RingBuffer<MyFileEvent> ringBuffer;

    public MyFileEventProducer(RingBuffer<MyFileEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    /**
     * onData用来发布事件，每调用一次发布一次事件
     * 它的参数会用过事件传递给消费者
     */
    public void onData(MyFile myFile) {
        // 1.可以把ringBuffer看做一个事件队列，那么next就是得到下面一个事件槽
        long sequence = ringBuffer.next();
        try {
            // 2.用上面的索引取出一个空的事件用于填充（获取该序号对应的事件对象）
            MyFileEvent event = ringBuffer.get(sequence);
            // 3.获取要通过事件传递的业务数据
            event.setMyFile(myFile);
        } finally {
            // 4.发布事件
            // 注意，最后的ringBuffer.publish 方法必须包含在finally中以确保必须得到调用，如果某个请求的sequence未被提交，
            ringBuffer.publish(sequence);
        }

    }
}
