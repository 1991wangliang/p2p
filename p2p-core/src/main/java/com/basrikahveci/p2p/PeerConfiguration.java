package com.basrikahveci.p2p;

import com.basrikahveci.p2p.config.P2PConfig;
import com.basrikahveci.p2p.peer.PeerHandle;
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
    public P2PConfig p2PConfig(){
        return new P2PConfig();
    }

    @Bean
    @ConditionalOnMissingBean
    public PeerHandle peerHandle(P2PConfig p2PConfig){
        return new PeerHandle(p2PConfig);
    }

}
