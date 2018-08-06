package com.miklesw.conway.grid;

import com.miklesw.conway.grid.events.GridEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GridConfig {

    @Bean
    GridManager gridManager(
            GridEventPublisher gridEventPublisher,
            @Value("${grid.size.x}") String gridSizeX,
            @Value("${grid.size.y}") String gridSizeY) {
        int x = Integer.parseInt(gridSizeX);
        int y = Integer.parseInt(gridSizeY);
        return new GridManager(gridEventPublisher, x, y);
    }
}
