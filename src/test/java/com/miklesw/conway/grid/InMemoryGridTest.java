package com.miklesw.conway.grid;

import com.miklesw.conway.grid.model.CellPosition;
import com.miklesw.conway.grid.model.CellState;
import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


public class InMemoryGridTest {

    @Test
    public void whenInitialised_thenWillGenerateGridOfDeadCellsOfSizeXY() {
        // given
        int gridSizeX = 6;
        int gridSizeY = 4;

        // when
        InMemoryGrid grid = new InMemoryGrid(gridSizeX, gridSizeY);

        // then
        Map<CellPosition, CellState> cells = grid.getCells();
        for (int x = 1; x <= gridSizeX; x++) {
            for (int y = 1; y <= gridSizeY; y++) {
                CellPosition position = new CellPosition(x, y);
                CellState cellState = cells.get(position);
                assertThat(cellState).isNotNull();
                assertThat(cellState.isLive()).isFalse();
                assertThat(cellState.getColor()).isNull();
            }
        }
    }

    // TODO: testing other methods



}