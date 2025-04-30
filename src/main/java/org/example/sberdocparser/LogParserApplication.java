package org.example.sberdocparser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class LogParserApplication {

    private static final Logger logger = LoggerFactory.getLogger(LogParserApplication.class);

    public static void main(String[] args) {

        logger.debug("This is a DEBUG message from LogParserApplication!");
        SpringApplication.run(LogParserApplication.class, args);
    }

}
