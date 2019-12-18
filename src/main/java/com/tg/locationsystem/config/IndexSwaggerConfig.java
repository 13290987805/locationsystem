package com.tg.locationsystem.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;

/**
 * @author hyy
 * @ Date2019/12/18
 */
@Configuration
public class IndexSwaggerConfig {

    /**
     * 配置文件上传大小
     */
    @Bean
    public MultipartConfigElement multipartConfigElement(){
        MultipartConfigFactory factory = new MultipartConfigFactory();
        /*//  单个数据大小 10M
        factory.setMaxFileSize("10240KB");*/
        /// 总上传数据大小 10M
        factory.setMaxRequestSize("10240KB");
        return factory.createMultipartConfig();
    }

}
