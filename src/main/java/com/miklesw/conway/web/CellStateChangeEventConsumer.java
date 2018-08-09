package com.miklesw.conway.web;

import com.miklesw.conway.events.CellStateChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class CellStateChangeEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CellStateChangeEventConsumer.class);

    @Async
    @EventListener
    public void onCellStateChanged(CellStateChangedEvent cellStateChangedEvent) {
        LOGGER.info("Handling cell state change event for cell at {} with {}", cellStateChangedEvent.getCellPosition(), cellStateChangedEvent.getCellState());
        // TODO: publish events
    }
}
