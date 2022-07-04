package com.example.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

@Component
public class MyTask {
    private static final SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

//    @Scheduled(fixedRate = 3000)
//    public void reportCurrentTime(){
//        System.out.println("NOW:"+sf.format(new Date()));
//    }
}
