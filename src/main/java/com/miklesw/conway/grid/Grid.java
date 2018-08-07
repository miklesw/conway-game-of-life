package com.miklesw.conway.grid;

import com.miklesw.conway.grid.model.CellPosition;
import com.miklesw.conway.grid.model.CellState;

import java.util.Map;

public interface Grid {

    /**
     * Locks for write operations for the current thread.
     */
    void lock();

    /**
     * Unlocks the grid
     */
    void unlock();

    /**
     * Returns a hashmap with all the cells in the grid.
     */
    Map<CellPosition, CellState> getCells();

    /**
     * Returns the cell state for a position in the grid.
     */
    CellState getCellState(CellPosition cellPosition);

    /**
     * Updates an cell state at an existing position. Attempts to update a non-existent position are ignored.
     *
     * NOTE: probably should throw an exception if the position is out of grid bounds.
     */
    void updateCellState(CellPosition cellPosition, CellState cellState);

    /**
     * Resets the grid state.
     */
    void reset();

    int getGridSizeX();

    int getGridSizeY();
}
