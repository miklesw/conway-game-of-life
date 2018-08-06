package com.miklesw.conway.grid.util;

import com.miklesw.conway.grid.CellPosition;
import org.junit.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class GridUtilsTest {

    @Test
    public void givenACellSurroundedByCells_whenFindingNeighbouringCells_then8NeighboursWillBeReturned() {
        // when
        Set<CellPosition> neighbouringCells = GridUtils.findNeighbouringCells(new CellPosition(3, 3), 6, 6);

        // then
        assertThat(neighbouringCells).containsOnly(
                new CellPosition(2,2),
                new CellPosition(3,2),
                new CellPosition(4,2),
                new CellPosition(2,3),
                new CellPosition(4,3),
                new CellPosition(2,4),
                new CellPosition(3,4),
                new CellPosition(4,4)
        );
    }


    @Test
    public void givenACellAtGridEdge_whenFindingNeighbouringCells_then6NeighboursWillBeReturned() {
        // when
        Set<CellPosition> rightEdgeCell = GridUtils.findNeighbouringCells(new CellPosition(6, 3), 6, 6);
        assertThat(rightEdgeCell).containsOnly(
                new CellPosition(6,2),
                new CellPosition(5,2),
                new CellPosition(5,3),
                new CellPosition(5,4),
                new CellPosition(6,4)
        );

        Set<CellPosition> leftEdgeCell = GridUtils.findNeighbouringCells(new CellPosition(1, 3), 6, 6);
        assertThat(leftEdgeCell).containsOnly(
                new CellPosition(1,2),
                new CellPosition(2,2),
                new CellPosition(2,3),
                new CellPosition(2,4),
                new CellPosition(1,4)
        );

        Set<CellPosition> topEdgeCell = GridUtils.findNeighbouringCells(new CellPosition(3, 1), 6, 6);
        assertThat(topEdgeCell).containsOnly(
                new CellPosition(2,1),
                new CellPosition(2,2),
                new CellPosition(3,2),
                new CellPosition(4,2),
                new CellPosition(4,1)
        );

        Set<CellPosition> bottomEdgeCell = GridUtils.findNeighbouringCells(new CellPosition(3, 6), 6, 6);
        assertThat(bottomEdgeCell).containsOnly(
                new CellPosition(2,6),
                new CellPosition(2,5),
                new CellPosition(3,5),
                new CellPosition(4,5),
                new CellPosition(4,6)
        );
    }


    @Test
    public void givenACellAtGridCorner_whenFindingNeighbouringCells_then3NeighboursWillBeReturned() {
        Set<CellPosition> topLeftCornerCell = GridUtils.findNeighbouringCells(new CellPosition(1, 1), 6, 6);
        assertThat(topLeftCornerCell).containsOnly(
                new CellPosition(1,2),
                new CellPosition(2,2),
                new CellPosition(2,1)
        );

        Set<CellPosition> bottomRightCornerCell = GridUtils.findNeighbouringCells(new CellPosition(6, 6), 6, 6);
        assertThat(bottomRightCornerCell).containsOnly(
                new CellPosition(5,5),
                new CellPosition(6,5),
                new CellPosition(5,6)
        );

        Set<CellPosition> topRightCornerCell = GridUtils.findNeighbouringCells(new CellPosition(6, 1), 6, 6);
        assertThat(topRightCornerCell).containsOnly(
                new CellPosition(5,1),
                new CellPosition(5,2),
                new CellPosition(6,2)
        );

        Set<CellPosition> bottomLeftCornerCell = GridUtils.findNeighbouringCells(new CellPosition(1, 6), 6, 6);
        assertThat(bottomLeftCornerCell).containsOnly(
                new CellPosition(1,5),
                new CellPosition(2,5),
                new CellPosition(2,6)
        );


    }
}