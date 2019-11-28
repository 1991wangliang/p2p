package com.codingapi.penetrationclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class PenetrationClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(PenetrationClientApplication.class, args);
    }

    @PostConstruct
    public void start(){
        Client client = new Client();
        client.start();
    }

}
