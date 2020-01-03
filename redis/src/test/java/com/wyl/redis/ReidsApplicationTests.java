package com.wyl.redis;

import com.wyl.redis.utils.RedisUtil;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReidsApplicationTests {

    @Autowired
    private RedisUtil redis;

    @Test
    void contextLoads() {
    }

    @Test
    public void redisTest(){
        redis.lpush(0,"test","aaa","bbb","ccc");
    }

}
