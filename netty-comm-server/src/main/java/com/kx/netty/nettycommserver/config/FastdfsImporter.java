package com.kx.netty.nettycommserver.config;

import com.github.tobato.fastdfs.FdfsClientConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.Import;
import org.springframework.jmx.support.RegistrationPolicy;

/**
 * 　　* @description: TODO
 * 　　* @author kx
 * 　　* @date 2020/03/21 20:42
 *
 */
@Configuration
@Import(FdfsClientConfig.class)
@EnableMBeanExport(registration= RegistrationPolicy.IGNORE_EXISTING)
public class FastdfsImporter {

}
