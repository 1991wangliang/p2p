package com.codingapi.p2p.peerd;

import com.codingapi.p2p.core.peer.PeerHandle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@Slf4j
@SpringBootApplication
public class P2pPeerDApplication {

    public static void main(String[] args) {
        SpringApplication.run(P2pPeerDApplication.class, args);
    }

    @Autowired
    private PeerHandle peerHandle;

    @PostConstruct
    public void start() throws InterruptedException {
        peerHandle.start();
        log.info("p2p network running...");
    }

}