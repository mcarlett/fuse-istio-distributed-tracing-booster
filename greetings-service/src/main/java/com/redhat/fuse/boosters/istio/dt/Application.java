package com.redhat.fuse.boosters.istio.dt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;


@SpringBootApplication
public class Application {

    @Value("${jaeger.reporter.endpoint}")
    private String jagerReporterEndpoint;


    /**
     * Main method to start the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
