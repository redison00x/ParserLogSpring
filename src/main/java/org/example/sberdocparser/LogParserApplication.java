package org.example.sberdocparser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class LogParserApplication {

    public static void main(String[] args) {

        SpringApplication.run(LogParserApplication.class, args);
    }

}
