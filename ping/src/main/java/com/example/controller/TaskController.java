package com.example.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;


@RestController
@EnableAsync
@RequestMapping("task")
public class TaskController {

    private final static Logger logger= LoggerFactory.getLogger(TaskController.class);
    @Resource
    private ThreadPoolTaskScheduler taskScheduler;

    private ScheduledFuture<?> scheduledFuture;

    @Value("${timing-task.cron1}")
    private String cronStr1;

    @Value("${timing-task.cron2}")
    private String cronStr2;

    @RequestMapping("/start")
    public Mono<String> startTask() {

        scheduledFuture =taskScheduler.schedule(new RunTask01(), new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                return new CronTrigger(cronStr1).nextExecutionTime(triggerContext);
            }
        });
        logger.info("start timed task success ..");
        return Mono.just("start task suceess");
    }
    @RequestMapping("/stop")
    public String stopTask() {
        Boolean result = null;
        if (scheduledFuture != null) {
            result = scheduledFuture.cancel(true);
        }
        logger.info("stop timed task result: " + result);
        return "stop task result: " + result;
    }
    @RequestMapping("/modify")
    public String modifyTask() {
        Boolean stopResult = null;
        // 停止定时任务
        if (scheduledFuture != null) {
            stopResult = scheduledFuture.cancel(true);
        } else {
            logger.info("modify task error -> scheduledFuture is null");
            return "error";
        }
        // 更换cron重新开启定时任务
        if (stopResult) {
            scheduledFuture = taskScheduler.schedule(new RunTask01(), new Trigger() {
                @Override
                public Date nextExecutionTime(TriggerContext triggerContext) {
                    return new CronTrigger(cronStr2).nextExecutionTime(triggerContext);
                }
            });
            logger.info("modify task success ..");
            return "success";
        }
        logger.info("modify task failed ..");
        return "failed";
    }

    @Async
    class RunTask01 implements Runnable {

        @Override
        public void run() {
            logger.info(Thread.currentThread().getName() + "|schedule task01" + "|" + new Date().toLocaleString());
        }
    }

    class RunTask02 implements Runnable {
        @Override
        public void run() {
            logger.info(Thread.currentThread().getName() + "|schedule task02" + "|" + new Date().toLocaleString());
        }
    }

}
