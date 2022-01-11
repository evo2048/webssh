package com.sda.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.sda.entities")
@EnableJpaRepositories("com.sda.repositories")
@ComponentScan("com.sda")
@Import({WebConfig.class, WebSecurityConfig.class})
public class AppConfig {
}
