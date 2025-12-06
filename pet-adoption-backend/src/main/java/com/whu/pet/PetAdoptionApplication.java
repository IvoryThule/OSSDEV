package com.whu.pet;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 宠物领养管理系统启动类
 */
@SpringBootApplication
@MapperScan("com.whu.pet.mapper")
public class PetAdoptionApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetAdoptionApplication.class, args);
        System.out.println("========================================");
        System.out.println("  宠物领养管理系统启动成功!");
        System.out.println("  访问地址: http://localhost:8080");
        System.out.println("  API文档: http://localhost:8080/swagger-ui.html");
        System.out.println("========================================");
    }
}
