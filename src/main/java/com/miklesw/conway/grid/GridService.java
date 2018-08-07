package com.miklesw.conway.grid;

import com.miklesw.conway.grid.events.GridEventPublisher;
import com.miklesw.conway.grid.model.CellPosition;
import com.miklesw.conway.grid.model.CellState;
import com.miklesw.conway.utils.ColorUtils;

import java.awt.*;
import java.util.*;
import java.util.List;

import static com.miklesw.conway.grid.util.GridUtils.determineNeighbouringCells;
import static java.util.stream.Collectors.toList;


public class GridService {

    private final Grid grid;

    private final GridEventPublisher gridEventPublisher;

    public GridService(Grid grid, GridEventPublisher gridEventPublisher) {
        this.grid = grid;
        this.gridEventPublisher = gridEventPublisher;
    }

    public void spawnCell(CellPosition position, Color color) {
        grid.lock();

        try {
            CellState currentState = grid.getCellState(position);

            if (currentState.isLive()) {
                String message = String.format("Cell at %s is already live with colour %s!", position, currentState.getColor());
                throw new IllegalStateException(message);
            }

            CellState liveState = CellState.live(color);
            grid.updateCellState(position, liveState);
            gridEventPublisher.publishCellChangedEvent(position, liveState);
        } finally {
            grid.unlock();
        }
    }

    private void killCell(CellPosition position) {
        grid.lock();

        try {
            grid.updateCellState(position, CellState.dead());
            gridEventPublisher.publishCellChangedEvent(position, CellState.dead());
        } finally {
            grid.unlock();
        }
    }

    public void computeNextState() {
        Set<CellPosition> cellsToKill = new HashSet<>();
        Map<CellPosition, Color> cellsToSpawn = new HashMap<>();

        grid.lock();
        try {
            // determine next state
            for (Map.Entry<CellPosition, CellState> gridEntry : grid.getCells().entrySet()) {
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

            // not handling runtime exception because:
            // - forEach swallows runtime exceptions
            // - locks will prevent race conditions when computing state
            cellsToSpawn.forEach(this::spawnCell);

        } finally {
            grid.unlock();
        }
    }

    private Color findAverageCellColor(List<CellState> cellStates) {
        List<Color> cellColors = cellStates.stream()
                .map(CellState::getColor)
                .collect(toList());
        return ColorUtils.average(cellColors);
    }

    private List<CellState> findLiveNeighbourCellStates(CellPosition cellPosition) {
        return determineNeighbouringCells(cellPosition, grid.getGridSizeX(), grid.getGridSizeY()).stream()
                .map(grid::getCellState)
                .filter(CellState::isLive)
                .collect(toList());
    }


}
