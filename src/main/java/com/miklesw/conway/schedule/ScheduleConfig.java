package com.miklesw.conway.schedule;

import com.miklesw.conway.grid.GridService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class ScheduleConfig {

    @Bean
    @ConditionalOnProperty(name = "grid.next.state.enabled", havingValue = "true", matchIfMissing = true)
    StateComputationScheduler stateComputationScheduler(GridService gridService) {
        return new StateComputationScheduler(gridService);
    }
}
