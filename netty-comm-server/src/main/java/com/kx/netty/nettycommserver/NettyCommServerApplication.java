package com.kx.netty.nettycommserver;

import com.kx.netty.nettycommserver.utils.SpringUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@MapperScan("com.kx.netty.nettycommserver.dao")
public class NettyCommServerApplication {

	@Bean
	public SpringUtil getSpringUtil() {
		return new SpringUtil();
	}
	public static void main(String[] args) {
		SpringApplication.run(NettyCommServerApplication.class, args);
	}

}
