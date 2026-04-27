package com.hypo.appstoreprice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * app store price application
 *
 * @author hypo
 * @date 2025-09-16
 */
@SpringBootApplication(exclude = {
        com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration.class
})
@EnableScheduling
@MapperScan("com.hypo.appstoreprice.mapper")
public class AppStorePriceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppStorePriceApplication.class, args);
    }

}
