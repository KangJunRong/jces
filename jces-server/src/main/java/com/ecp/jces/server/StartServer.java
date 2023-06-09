package com.ecp.jces.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 注释说明
 */
@EnableScheduling
@SpringBootApplication
@MapperScan("com.ecp.jces.server.dc.mapper")
public class StartServer {
	public static void main(String[] args) {
		SpringApplication.run(StartServer.class, args);
	}
}
