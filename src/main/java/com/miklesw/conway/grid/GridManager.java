package com.miklesw.conway.grid;

import com.google.common.collect.ImmutableMap;
import com.miklesw.conway.grid.events.GridEventPublisher;
import com.miklesw.conway.utils.ColorUtils;

import java.awt.Color;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.miklesw.conway.grid.util.GridUtils.findNeighbouringCells;
import static java.util.stream.Collectors.toList;

public class GridManager {

    private Map<CellPosition, CellState> grid;

    private final GridEventPublisher gridEventPublisher;

    private final int gridSizeX;

    private final int gridSizeY;

    public GridManager(GridEventPublisher gridEventPublisher, int gridSizeX, int gridSizeY) {
        this.gridEventPublisher = gridEventPublisher;
        this.gridSizeX = gridSizeX;
        this.gridSizeY = gridSizeY;
        generateGrid(gridSizeX, gridSizeY);
    }

    public Map<CellPosition, CellState> getGrid() {
        return ImmutableMap.copyOf(grid);
    }

    public void spawnCell(CellPosition position, Color color) {
        CellState currentState = grid.get(position);

        if (currentState.isLive()) {
            String message = String.format("Cell at %s is already live with colour %s!", position, currentState.getColor());
            throw new IllegalStateException(message);
        }

        CellState liveState = CellState.live(color);
        grid.put(position, liveState);
        gridEventPublisher.publishCellChangedEvent(position, liveState);
    }

    private void killCell(CellPosition position) {
        grid.put(position, CellState.dead());
        gridEventPublisher.publishCellChangedEvent(position, CellState.dead());
    }

    public void nextState() {
        Set<CellPosition> cellsToKill = new HashSet<>();
        Map<CellPosition, Color> cellsToSpawn = new HashMap<>();

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
        // TODO: handling of exception?
        cellsToSpawn.forEach(this::spawnCell);
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

    private void generateGrid(int gridSizeX, int gridSizeY) {
        this.grid = new ConcurrentHashMap<>();
        for (int x = 1; x <= gridSizeX; x++) {
            for (int y = 1; y <= gridSizeY; y++) {
                CellPosition position = new CellPosition(x, y);
                grid.put(position, CellState.dead());
            }
        }
    }
}
