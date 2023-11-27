package com.fuHoi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @title: ServiceAuthApplication
 * @Author Xie
 * @Date: 2023/5/25 20:33
 * @Version 1.0
 */
@SpringBootApplication
@MapperScan("com.fuHoi.*.mapper")
public class ServiceAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceAuthApplication.class, args);
    }
}
