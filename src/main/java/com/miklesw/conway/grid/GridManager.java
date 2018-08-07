package com.miklesw.conway.grid;

import com.google.common.collect.ImmutableMap;
import com.miklesw.conway.grid.events.GridEventPublisher;
import com.miklesw.conway.utils.ColorUtils;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.miklesw.conway.grid.util.GridUtils.findNeighbouringCells;
import static java.util.stream.Collectors.toList;


public class GridManager {

    private Map<CellPosition, CellState> grid;

    private final Lock gridState = new ReentrantLock();

    private final GridEventPublisher gridEventPublisher;

    private final int gridSizeX;

    private final int gridSizeY;


    public GridManager(GridEventPublisher gridEventPublisher, int gridSizeX, int gridSizeY) {
        this.gridEventPublisher = gridEventPublisher;
        this.gridSizeX = gridSizeX;
        this.gridSizeY = gridSizeY;
        generateGrid();
    }

    public Map<CellPosition, CellState> getGrid() {
        return ImmutableMap.copyOf(grid);
    }

    public void spawnCell(CellPosition position, Color color) {
        gridState.lock();

        try {
            CellState currentState = grid.get(position);

            if (currentState.isLive()) {
                String message = String.format("Cell at %s is already live with colour %s!", position, currentState.getColor());
                throw new IllegalStateException(message);
            }

            CellState liveState = CellState.live(color);
            grid.put(position, liveState);
            gridEventPublisher.publishCellChangedEvent(position, liveState);
        } finally {
            gridState.unlock();
        }
    }

    private void killCell(CellPosition position) {
        gridState.lock();

        try {
            grid.put(position, CellState.dead());
            gridEventPublisher.publishCellChangedEvent(position, CellState.dead());
        } finally {
            gridState.unlock();
        }
    }

    public void nextState() {
        Set<CellPosition> cellsToKill = new HashSet<>();
        Map<CellPosition, Color> cellsToSpawn = new HashMap<>();

        gridState.lock();
        try {
            // determine next state
            for (Map.Entry<CellPosition, CellState> gridEntry : this.grid.entrySet()) {
                CellPosition cellPosition = gridEntry.getKey();
                CellState cellState = gridEntry.getValue();

                List<CellState> liveNeighbours = findLiveNeighbourCellStates(cellPosition);
                int liveNeighbourCount = liveNeighbours.size();

                if (cellState.isLive() && (liveNeighbourCount < 2 || liveNeighbourCount > 3)) {
                    cellsToKill.add(cellPosition);
                } else if (!cellState.isLive() && liveNeighbourCount == 3) {
                    cellsToSpawn.put(cellPosition, findAverageCellColor(liveNeighbours));
                }
            }

            // apply state
            cellsToKill.forEach(this::killCell);
            cellsToSpawn.forEach(this::spawnCell);

        } finally {
            gridState.unlock();
        }
    }

    private Color findAverageCellColor(List<CellState> cellStates) {
        List<Color> cellColors = cellStates.stream()
                .map(CellState::getColor)
                .collect(toList());
        return ColorUtils.average(cellColors);
    }

    private List<CellState> findLiveNeighbourCellStates(CellPosition cellPosition) {
        return findNeighbouringCells(cellPosition, gridSizeX, gridSizeY).stream()
                .map(grid::get)
                .filter(CellState::isLive)
                .collect(toList());
    }

    private void generateGrid() {
        this.grid = new ConcurrentHashMap<>();
        for (int x = 1; x <= gridSizeX; x++) {
            for (int y = 1; y <= gridSizeY; y++) {
                CellPosition position = new CellPosition(x, y);
                grid.put(position, CellState.dead());
            }
        }
    }
}
