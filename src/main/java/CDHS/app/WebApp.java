package CDHS.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan(basePackages = {"CDHS.controller"})
public class WebApp {
    public static void main(String[] args) {
        //启动springboot项目
        SpringApplication.run(WebApp.class,args);
    }
}
