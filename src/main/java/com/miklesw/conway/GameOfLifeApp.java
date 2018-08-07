package com.miklesw.conway;

import com.miklesw.conway.events.EventConfiguration;
import com.miklesw.conway.grid.GridConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@Import({
        GridConfiguration.class,
        EventConfiguration.class
})
@EnableWebMvc
@EnableAsync
public class GameOfLifeApp {

    public static void main(String[] args) {
        SpringApplication.run(GameOfLifeApp.class, args);
    }
}
