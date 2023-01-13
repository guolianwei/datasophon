package com.datasophon.api;

import akka.actor.*;
import com.datasophon.api.master.ActorUtils;
import com.datasophon.api.master.MasterServer;
import com.datasophon.api.master.alert.HostCheckActor;
import com.datasophon.api.master.alert.ServiceRoleCheckActor;
import com.datasophon.common.cache.CacheUtils;
import com.datasophon.common.command.HostCheckCommand;
import com.datasophon.common.command.ServiceRoleCheckCommand;
import com.datasophon.common.utils.HostUtils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.annotation.PostConstruct;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.datasophon.common.Constants.SYSPRO_NAME_HOSTNAME;

@SpringBootApplication(scanBasePackages = "com.datasophon.*")
@ServletComponentScan
@ComponentScan("com.datasophon")
@MapperScan("com.datasophon.dao")
@EnableWebMvc
public class DDHApplicationServer extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(DDHApplicationServer.class, args);
    }

    @PostConstruct
    public void run() throws UnknownHostException {
        String hostName = HostUtils.fetchHostName();
        CacheUtils.put("hostname", hostName);
        ActorUtils.init();
    }
}
