package com.miklesw.conway.grid;

import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;


public class GridManagerTest {

    private GridManager gridManager;

    @Before
    public void init() {
        gridManager = new GridManager(null,6,4);
    }

    @Test
    public void whenInitialised_thenWillGenerateGridOfDeadCellsOfSizeXY() {
        // given
        int gridSizeX = 6;
        int gridSizeY = 4;

        // when
        GridManager gridManager = new GridManager(null, gridSizeX, gridSizeY);

        // then
        Map<CellPosition, CellState> grid = gridManager.getGrid();
        for (int x = 1; x <= gridSizeX; x++) {
            for (int y = 1; y <= gridSizeY; y++) {
                CellPosition position = new CellPosition(x, y);
                CellState cellState = grid.get(position);
                assertThat(cellState).isNotNull();
                assertThat(cellState.isLive()).isFalse();
                assertThat(cellState.getColor()).isNull();
            }
        }
    }

    @Test
    public void givenADeadCell_whenSpawningACell_thenCellStateAndColorWillBeUpdated() {
        // given
        CellPosition position = new CellPosition(3, 3);
        Color green = Color.GREEN;

        // when
        gridManager.spawnCell(position, green);

        // then
        CellState updatedCellState = gridManager.getGrid().get(position);
        assertThat(updatedCellState.isLive()).isTrue();
        assertThat(updatedCellState.getColor()).isEqualTo(green);
    }

    @Test
    public void givenALiveCell_whenSpawningACell_WillThrowAnIllegalStateException() {
        // given
        CellPosition position = new CellPosition(3, 3);

        Color initialColor = Color.RED;
        gridManager.spawnCell(position, initialColor);

        Color color = Color.GREEN;

        // when
        Throwable thrown = catchThrowable(() -> gridManager.spawnCell(position, color));

        // when
        assertThat(thrown).isInstanceOf(IllegalStateException.class)
                .hasMessage("CellState at %s is already live with colour %s!", position, initialColor);

        CellState updatedCellState = gridManager.getGrid().get(position);
        assertThat(updatedCellState.isLive()).isTrue();
        assertThat(updatedCellState.getColor()).isEqualTo(initialColor);
        
    }

}