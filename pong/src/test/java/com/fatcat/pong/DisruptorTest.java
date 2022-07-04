package com.fatcat.pong;

import com.fatcat.pong.disruptor.MyFile;
import com.fatcat.pong.service.BasicEventService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Slf4j
public class DisruptorTest {

    @Autowired
    BasicEventService basicEventService;

    @Test
    public void test(){
        log.info("start publich test");

        int count = 100;

        for(int i=0;i<count;i++) {
            log.info("publich {}", i);
            basicEventService.publish(new MyFile(String.valueOf(i),String.valueOf(i)));
        }

        // 异步消费，因此需要延时等待
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 消费的事件总数应该等于发布的事件数
        assertEquals(count, basicEventService.eventCount());
    }
}
