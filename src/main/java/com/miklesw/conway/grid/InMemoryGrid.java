package com.miklesw.conway.grid;

import com.google.common.collect.ImmutableMap;
import com.miklesw.conway.grid.model.CellPosition;
import com.miklesw.conway.grid.model.CellState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class InMemoryGrid implements Grid {

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryGrid.class);

    private static final String ERROR_INVALID_GRID_SIZE = "Grid size must be at least 2 x 2";

    private final Map<CellPosition, CellState> grid = new ConcurrentHashMap<>();

    private final Lock lock = new ReentrantLock();

    private final int gridSizeX;

    private final int gridSizeY;

    public InMemoryGrid(int gridSizeX, int gridSizeY) {
        Assert.isTrue(gridSizeX > 1 && gridSizeY > 1 , ERROR_INVALID_GRID_SIZE);
        this.gridSizeX = gridSizeX;
        this.gridSizeY = gridSizeY;
        initialize();
    }

    @Override
    public Map<CellPosition, CellState> getCells() {
        return ImmutableMap.copyOf(grid);
    }

    @Override
    public CellState getCellState(CellPosition cellPosition) {
        return grid.get(cellPosition);
    }

    @Override
    public void updateCellState(CellPosition cellPosition, CellState cellState) {
        // TODO: could add additional check to throw ex if cell position is not in the map.
        grid.replace(cellPosition, cellState);
    }

    @Override
    public void lock() {
        LOGGER.debug("Acquiring grid lock.");
        lock.lock();
    }

    @Override
    public void unlock() {
        LOGGER.debug("Releasing grid lock.");
        lock.unlock();
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException("Not a requirements");
    }

    @Override
    public int getGridSizeX() {
        return gridSizeX;
    }

    @Override
    public int getGridSizeY() {
        return gridSizeY;
    }

    private void initialize() {
        LOGGER.info("Initializing Grid...");
        grid.clear();
        for (int x = 1; x <= gridSizeX; x++) {
            for (int y = 1; y <= gridSizeY; y++) {
                CellPosition position = new CellPosition(x, y);
                grid.put(position, CellState.dead());
            }
        }
    }
}
