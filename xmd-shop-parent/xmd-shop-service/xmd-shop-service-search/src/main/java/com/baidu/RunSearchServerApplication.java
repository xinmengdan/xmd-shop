package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @ClassName RunSearchServerApplication
 * @Description: TODO
 * @Author xinmengdan
 * @Date 2020/9/16
 * @Version V1.0
 **/
//exclude = {DataSourceAutoConfiguration.class} 避免加载不必要的自动化配置
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableEurekaClient
@EnableFeignClients
public class RunSearchServerApplication {


    public static void main(String[] args) {
        SpringApplication.run(RunSearchServerApplication.class);
    }

}
