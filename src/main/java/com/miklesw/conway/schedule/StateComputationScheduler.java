package com.miklesw.conway.schedule;

import com.miklesw.conway.grid.GridService;
import org.springframework.scheduling.annotation.Scheduled;

public class StateComputationScheduler {

    private final GridService gridService;

    public StateComputationScheduler(GridService gridService) {
        this.gridService = gridService;
    }

    @Scheduled(fixedDelayString = "${grid.next.state.interval.ms}")
    public void computeState() {
        gridService.computeNextState();
    }
}
