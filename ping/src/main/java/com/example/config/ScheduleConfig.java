package com.example.config;

import com.example.controller.MyTestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ScheduleConfig implements SchedulingConfigurer {

    private static final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    private static final SimpleDateFormat sfname = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
    private final static Logger logger= LoggerFactory.getLogger(ScheduleConfig.class);
    @Value("${myTask.path}")
    private String path;
    // 5s
    @Value("${timing-task.cron2}")
    private String cron2;
    // 1s
    @Value("${timing-task.cron1}")
    private String cron1;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
//        taskRegistrar.setTaskScheduler(taskScheduler());
//
//        taskRegistrar.getScheduler().schedule(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println(Thread.currentThread().getName() + "|SchedulingConfigurer cron task01");
//            }
//        }, new CronTrigger("0/3 * * * * ?"));


        // 使用默认线程池
        taskRegistrar.addCronTask(new CronTask(new Runnable() {
            @Override
            public void run() {
                Mono.fromSupplier(()->createFile()).subscribe();
                logger.info(sf.format(new Date())+"|"+Thread.currentThread().getName() + "|SchedulingConfigurer cron task02");
            }
        }, new CronTrigger(cron2)));
    }
    private String createFile(){
        try {
            int random=new Random().nextInt(10000);
            FileWriter fileWriter = new FileWriter(new File(path + sfname.format(new Date()) +random+ ".txt").getPath());
//                    System.out.println(sf.format(new Date()));
            Flux.just("hello").subscribe((t) -> {
                try {
                    fileWriter.write(t);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (fileWriter != null) {
                            fileWriter.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "finish";
    }

    /**
     * 自定义线程池
     * @return
     */
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(10);
        taskScheduler.setThreadNamePrefix("spring-task-scheduler-thread-");
        taskScheduler.setAwaitTerminationSeconds(60);
        taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        taskScheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        return taskScheduler;

    }
}
