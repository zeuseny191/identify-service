package com.phong.identify_service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.phong.identify_service.repository")
public class IdentifyServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdentifyServiceApplication.class, args);
	}

}
