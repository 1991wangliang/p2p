package com.codingapi.p2p.peera.controller;

import com.codingapi.p2p.core.config.P2PConfig;
import com.codingapi.p2p.core.peer.PeerHandle;
import com.codingapi.p2p.core.peer.network.Connection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author lorne
 * @date 2019/11/27
 * @description
 */
@RestController
@Slf4j
public class DemoController {

    @Autowired
    private PeerHandle peerHandle;

    @GetMapping("/connect")
    public boolean connect(@RequestParam("host") String host,@RequestParam("port") int port){
        peerHandle.connect(host, port);
        log.info("connect ->{}:{}",host,port);
        return true;
    }

    @GetMapping("/broadcast")
    public boolean broadcast(@RequestParam("msg") String msg){
        peerHandle.broadcast(msg);
        log.info("broadcast ->{}",msg);
        return true;
    }

    @GetMapping("/send")
    public boolean send(@RequestParam("peer") String peer,@RequestParam("msg") String msg){
        peerHandle.send(peer,msg);
        log.info("send to {} ,say:'{}'",peer,msg);
        return true;
    }

    @GetMapping("/leave")
    public boolean leave(){
        peerHandle.leave();
        log.info("p2p network leave");
        return true;
    }

    @GetMapping("/disconnect")
    public boolean disconnect(@RequestParam("peer") String peer){
        peerHandle.disconnect(peer);
        log.info("disconnect peer:{}",peer);
        return true;
    }

    @GetMapping("/list")
    public List<P2PConfig> list(){
        Collection<Connection> connections =  peerHandle.connections();
        List<P2PConfig> list = new ArrayList<>();
        for (Connection connection:connections){
            list.add(new P2PConfig(connection.getPeerName(),connection.getRemoteAddress().getPort()));
        }
        return list;
    }
}
