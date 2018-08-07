package com.miklesw.conway.grid;

import com.miklesw.conway.grid.events.GridEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GridConfiguration {

    @Bean
    @ConditionalOnProperty(name = "grid.impl", havingValue = "memory", matchIfMissing = true)
    Grid inMemoryGrid(@Value("${grid.size.x}") String gridSizeX, @Value("${grid.size.y}") String gridSizeY) {
        int x = Integer.parseInt(gridSizeX);
        int y = Integer.parseInt(gridSizeY);
        return new InMemoryGrid(x, y);
    }

    @Bean
    GridService gridService(Grid grid, GridEventPublisher gridEventPublisher) {
        return new GridService(grid, gridEventPublisher);
    }
}
