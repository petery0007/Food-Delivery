package com.infosystem.greenfooddelivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class GreenFoodDeliveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(GreenFoodDeliveryApplication.class, args);
    }

}
