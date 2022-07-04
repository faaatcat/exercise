package com.fatcat.pong;

import com.fatcat.pong.service.WatchFileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ReadFileTest {
    @Autowired
    WatchFileService watchFileService;

    @Test
    public void test(){
        watchFileService.initWatch();
    }
}
