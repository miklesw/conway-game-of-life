package com.miklesw.conway.grid.events;

import com.miklesw.conway.grid.CellState;
import com.miklesw.conway.grid.CellPosition;

public interface GridEventPublisher {

    void publishCellChangedEvent(CellPosition position, CellState cellState);

}
