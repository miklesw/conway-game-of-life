package com.miklesw.conway.grid.events;

import com.miklesw.conway.grid.model.CellState;
import com.miklesw.conway.grid.model.CellPosition;

public class CellStateChangedEvent {

    private final CellPosition cellPosition;
    private final CellState cellState;

    public CellStateChangedEvent(CellPosition cellPosition, CellState cellState) {
        this.cellPosition = cellPosition;
        this.cellState = cellState;
    }

    public CellPosition getCellPosition() {
        return cellPosition;
    }

    public CellState getCellState() {
        return cellState;
    }
}
