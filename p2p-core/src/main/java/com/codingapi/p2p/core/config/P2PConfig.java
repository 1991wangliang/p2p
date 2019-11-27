package com.codingapi.p2p.core.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lorne
 * @date 2019-11-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class P2PConfig {

    /**
     * peer name
     */
    private String peerName;
    /**
     * peer port
     */
    private int port;
}
