package com.infy.ekart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.infy.ekart.entity.Customer;
import com.infy.ekart.entity.VerificationToken;
import com.infy.ekart.repository.CustomerRepository;
@EnableDiscoveryClient
@SpringBootApplication
public class AuthServiceApplication {

    public static void main(String[] args) {
    	ConfigurableApplicationContext context=SpringApplication.run(AuthServiceApplication.class, args);
    }

}
