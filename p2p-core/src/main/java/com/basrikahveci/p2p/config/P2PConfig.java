package com.basrikahveci.p2p.config;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lorne
 * @date 2019-11-07
 * @description
 */
@Data
@NoArgsConstructor
public class P2PConfig {

    /**
     * Peer名称
     */
    private String peerName;
    /**
     * 绑定的端口
     */
    private int port;
}
