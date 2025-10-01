package com.coder;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.coder.mapper")
public class SeckillProjectServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeckillProjectServiceApplication.class, args);
    }

}
