package com.lpy;


import com.lpy.utils.RedisOperator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Slf4j
@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisOperator redisOperator;


    @Test
    public void redisTest(){
        redisOperator.set("name","lpy");
        log.info(redisOperator.get("name"));
    }



}
