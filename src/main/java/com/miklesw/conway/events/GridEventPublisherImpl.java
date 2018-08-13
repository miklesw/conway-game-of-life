package com.miklesw.conway.events;

import com.miklesw.conway.grid.events.GridEventPublisher;
import com.miklesw.conway.grid.model.CellPosition;
import com.miklesw.conway.grid.model.CellState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;


public class GridEventPublisherImpl implements GridEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(GridEventPublisherImpl.class);

    private final ApplicationEventPublisher eventPublisher;

    public GridEventPublisherImpl(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void publishCellChangedEvent(CellPosition position, CellState state) {
        LOGGER.debug("Publishing cell change event for {} with {}", position, state);
        eventPublisher.publishEvent(new CellStateChangedEvent(position, state));
    }
}
