package com.codingapi.penetrationserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lorne
 * @date 2019/11/28
 * @description
 */
@RestController
public class DemoController {

    @GetMapping("/hi")
    public int hi(){
        return 1000;
    }

}
