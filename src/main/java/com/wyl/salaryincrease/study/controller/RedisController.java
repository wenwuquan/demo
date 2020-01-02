package com.wyl.salaryincrease.study.controller;

import com.wyl.salaryincrease.study.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/redis")
public class RedisController {
    private Logger LOG = LoggerFactory.getLogger(RedisController.class);

    @Autowired
    private RedisUtil redis;

    @GetMapping("/getredis")
    public void getRedis(){
        List<String> test = redis.bRpop("test", 0);
        System.out.println(test);
        System.out.println(test.get(0));
        System.out.println(test.get(1));
    }
}
