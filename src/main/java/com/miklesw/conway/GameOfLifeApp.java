package com.miklesw.conway;

import com.miklesw.conway.events.EventConfig;
import com.miklesw.conway.grid.GridConfig;
import com.miklesw.conway.web.WebConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({
        GridConfig.class,
        EventConfig.class,
        WebConfig.class
})
public class GameOfLifeApp {

    public static void main(String[] args) {
        SpringApplication.run(GameOfLifeApp.class, args);
    }
}
