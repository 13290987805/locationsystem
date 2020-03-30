package com.tg.locationsystem.config;

import com.tg.locationsystem.utils.influxDBUtil.InfluxDBConnection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxDbConfig {

    @Value("${spring.influx.url:''}")
    private String influxDBUrl;
    @Value("${spring.influx.user:''}")
    private String userName;

    @Value("${spring.influx.password:''}")
    private String password;

    @Value("${spring.influx.database:''}")
    private String database;

    @Bean
    public InfluxDBConnection influxDbUtils() {
        //System.out.println(influxDBUrl + userName + password + database);
        InfluxDBConnection influxDBConnection = new InfluxDBConnection(userName, password, influxDBUrl, database, "oneYear");
        System.out.println("========连接influxdb数据库成功："+influxDBConnection.ping());
        return influxDBConnection;
    }
}

