package com.miklesw.conway.grid.events;

import com.miklesw.conway.grid.model.CellState;
import com.miklesw.conway.grid.model.CellPosition;

public interface GridEventPublisher {

    void publishCellChangedEvent(CellPosition position, CellState cellState);

}
