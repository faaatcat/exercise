package com.example.controller;

import com.example.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class MyTestController {

    private final static Logger logger= LoggerFactory.getLogger(MyTestController.class);

    @GetMapping("/hello")
    public String hello(){
        logger.info("1111111");
        logger.warn("12222222");

        return "hello world";
    }

    @GetMapping("/user")
    public Mono<User> getUser(){
        User user=new User();
        user.setName("xiaoming");
        user.setDesc("nande");
        return Mono.just(user);
    }
}
