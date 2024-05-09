package com.example.hello;

import com.example.hello.controller.hellocontroller;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;

@SpringBootApplication
public class HelloApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelloApplication.class, args);
	}

	@Bean
	public CommandLineRunner initController(hellocontroller controller) {
		return args -> {
			if (args.length > 0) {
				String filePath = args[0];
				File file = new File(filePath);
				if (!file.exists() || !file.isFile()) {
					System.out.println(filePath + " not a valid file path.");
				}
				controller.initialize(args[0]);
			}
		};
	}
}
