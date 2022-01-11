package com.sda;

import com.sda.config.AppConfig;
import org.springframework.boot.SpringApplication;

public class App 
{
    public static void main( String[] args )
    {
        SpringApplication.run(AppConfig.class, args);
    }
}
