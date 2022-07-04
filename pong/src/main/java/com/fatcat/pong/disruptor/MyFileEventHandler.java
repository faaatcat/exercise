package com.fatcat.pong.disruptor;


import com.lmax.disruptor.EventHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

/**
 * 通过实现接口EventHandler 定义事件处理的具体实现（消费）
 */
@Slf4j
public class MyFileEventHandler implements EventHandler<MyFileEvent> {

    private Consumer<?> consumer;

    public MyFileEventHandler(){
    }

    public MyFileEventHandler(Consumer<?> consumer){
        this.consumer=consumer;
    }

    @Override
    public void onEvent(MyFileEvent myFileEvent, long l, boolean b) throws Exception {
        log.info("sequence [{}], endOfBatch [{}], event : {}",l,b,myFileEvent);
//        Thread.sleep(100);
        if(null!=consumer){
            consumer.accept(null);
        }
//        System.out.println(myFileEvent.getMyFile().toString());
    }
}
