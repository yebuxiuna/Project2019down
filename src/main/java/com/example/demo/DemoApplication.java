package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@SpringBootApplication(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class DemoApplication {

	public static Logger logger = LoggerFactory.getLogger("spring-boot:");

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
