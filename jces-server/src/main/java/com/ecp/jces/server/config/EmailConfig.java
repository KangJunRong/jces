package com.ecp.jces.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @program: demo
 * @description:
 * @author: KJR
 * @create: 2020-03-17 11:19
 **/
@Configuration
public class EmailConfig {

    public static String MAIL_HOST;
    public static String MAIL_PORT;
    public static String MAIL_USER;
    public static String MAIL_PASSWORD;

    @Value("${mail.host}")
    public String mailHost;
    @Value("${mail.port}")
    public String mailPort;
    @Value("${mail.user}")
    public String mailUser;
    @Value("${mail.password}")
    public String mailPassword;


    @PostConstruct
    public void init() {
        EmailConfig.MAIL_HOST = mailHost;
        EmailConfig.MAIL_PORT = mailPort;
        EmailConfig.MAIL_USER = mailUser;
        EmailConfig.MAIL_PASSWORD = mailPassword;
    }
}
