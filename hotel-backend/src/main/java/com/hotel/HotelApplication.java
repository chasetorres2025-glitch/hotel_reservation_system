package com.hotel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 酒店管理系统启动类
 */
@SpringBootApplication
public class HotelApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelApplication.class, args);
        System.out.println("==========================================");
        System.out.println("酒店管理系统启动成功！");
        System.out.println("接口文档地址: http://localhost:8080");
        System.out.println("Druid监控地址: http://localhost:8080/druid");
        System.out.println("==========================================");
    }
}
