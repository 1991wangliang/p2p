package com.codingapi.p2p.core;

import com.codingapi.p2p.core.config.P2PConfig;
import com.codingapi.p2p.core.peer.PeerEventLoopGroup;
import com.codingapi.p2p.core.peer.PeerHandle;
import com.codingapi.p2p.core.peer.service.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author lorne
 * @date 2019/11/27
 * @description
 */
@Configuration
@ComponentScan
public class PeerConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "p2p")
    @ConditionalOnMissingBean
    public P2PConfig p2PConfig(){
        return new P2PConfig();
    }

    @Bean
    @ConditionalOnMissingBean
    public PeerEventLoopGroup peerEventLoopGroup(P2PConfig p2PConfig){
        return new PeerEventLoopGroup(p2PConfig);
    }

    @Bean
    @ConditionalOnMissingBean
    public ConnectionService connectionService(PeerEventLoopGroup peerEventLoopGroup){
        return new ConnectionService(peerEventLoopGroup);
    }

    @Bean
    @ConditionalOnMissingBean
    public LeadershipService leadershipService(ConnectionService connectionService,PeerEventLoopGroup peerEventLoopGroup){
        return new LeadershipService(connectionService,peerEventLoopGroup);
    }

    @Bean
    @ConditionalOnMissingBean
    public IPingService pingService(ConnectionService connectionService,LeadershipService leadershipService,PeerEventLoopGroup peerEventLoopGroup){
        return new NoForwardPingService(connectionService,leadershipService,peerEventLoopGroup);
    }

    @Bean
    @ConditionalOnMissingBean
    public PeerHandle peerHandle(P2PConfig p2PConfig, PeerEventLoopGroup peerEventLoopGroupBean, ConnectionService connectionService, LeadershipService leadershipService, IPingService pingService){
        return new PeerHandle(p2PConfig,peerEventLoopGroupBean,connectionService,leadershipService,pingService);
    }

}
