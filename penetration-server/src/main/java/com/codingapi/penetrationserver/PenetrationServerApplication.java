package com.codingapi.penetrationserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class PenetrationServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PenetrationServerApplication.class, args);
    }

    @PostConstruct
    public void start() throws InterruptedException {
        Server server = new Server();
        server.start();
    }

}
