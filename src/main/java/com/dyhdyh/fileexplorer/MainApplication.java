package com.dyhdyh.fileexplorer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * author  dengyuhan
 * created 2018/5/22 13:52
 */
@ComponentScan(basePackages = {
        "com.dyhdyh.fileexplorer.controller",
        "com.dyhdyh.fileexplorer.service",
        "com.dyhdyh.fileexplorer.filter"})
@EnableAutoConfiguration
@SpringBootApplication
public class MainApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(MainApplication.class, args);
    }

}
