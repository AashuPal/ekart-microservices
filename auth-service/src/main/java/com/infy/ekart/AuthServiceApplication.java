package com.infy.ekart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.infy.ekart.entity.Customer;
import com.infy.ekart.repository.CustomerRepository;

@SpringBootApplication
public class AuthServiceApplication {

    public static void main(String[] args) {
    	ConfigurableApplicationContext context=SpringApplication.run(AuthServiceApplication.class, args);
    	CustomerRepository cr=context.getBean(CustomerRepository.class);
    	cr.findAll().forEach(System.out::println);
    }

}
