package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @ClassName RunBannerServerApplication
 * @Description: TODO
 * @Author xinmengdan
 * @Date 2020/10/27
 * @Version V1.0
 **/
@SpringBootApplication
@EnableEurekaClient
@MapperScan("com.baidu.shop.mapper")
@EnableFeignClients
public class RunBannerServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RunBannerServerApplication.class);
    }

}
