package com.kx.netty.nettycommserver.config;

import org.n3r.idworker.Sid;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 　　* @description: TODO
 * 　　* @author kx
 * 　　* @date 2020/03/21 23:36
 *
 */
@Configuration
public class SidProbably {

    @Bean(value = "sid")
    public Sid getSid(){
        return new Sid();
    }
}
