package com.miklesw.conway.grid;

import com.miklesw.conway.grid.events.GridEventPublisher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.awt.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GridManagerStateComputationTest {

    private GridManager gridManager;

    @Mock
    private GridEventPublisher gridEventPublisher;

    @Before
    public void init() {
        reset(gridEventPublisher);
        gridManager = new GridManager(gridEventPublisher,6,6);
    }

    @Test
    public void givenALiveCellWithNoNeighbours_whenComputingNextState_thenCellWillDie() {
        // given
        CellPosition cellPosition = new CellPosition(3, 3);
        gridManager.spawnCell(cellPosition, Color.RED);

        // when
        gridManager.nextState();

        // then
        long liveCellCount = gridManager.getGrid().entrySet()
                .stream()
                .filter(e -> e.getValue().isLive())
                .count();
        assertThat(liveCellCount).isEqualTo(0);

        CellState cellWithNeighboursState = gridManager.getGrid().get(cellPosition);
        assertThat(cellWithNeighboursState.isLive()).isFalse();
        assertThat(cellWithNeighboursState.getColor()).isEqualTo(null);

        verify(gridEventPublisher, times(1)).publishCellChangedEvent(eq(cellPosition), eq(CellState.dead()));
    }

    @Test
    public void givenALiveCellWith1Neighbour_whenComputingNextState_thenCellWillDie() {
        // given
        CellPosition cellWithNeighboursPosition = new CellPosition(3, 3);
        gridManager.spawnCell(cellWithNeighboursPosition, Color.RED);
        gridManager.spawnCell(new CellPosition(2,3), Color.RED);

        // when
        gridManager.nextState();

        // then
        long liveCellCount = gridManager.getGrid().entrySet()
                .stream()
                .filter(e -> e.getValue().isLive())
                .count();
        assertThat(liveCellCount).isEqualTo(0);

        CellState cellWithNeighboursState = gridManager.getGrid().get(cellWithNeighboursPosition);
        assertThat(cellWithNeighboursState.isLive()).isFalse();
        assertThat(cellWithNeighboursState.getColor()).isNull();

        verify(gridEventPublisher, times(1)).publishCellChangedEvent(eq(cellWithNeighboursPosition), eq(CellState.dead()));
    }


    @Test
    public void givenALiveCellWith2Neighbours_whenComputingNextState_thenCellWillSurvive() {
        // given
        CellPosition cellWithNeighboursPosition = new CellPosition(3, 3);
        gridManager.spawnCell(cellWithNeighboursPosition, Color.RED);
        gridManager.spawnCell(new CellPosition(2,2), Color.GREEN);
        gridManager.spawnCell(new CellPosition(4,4), Color.BLUE);
        reset(gridEventPublisher);

        // when
        gridManager.nextState();

        // then
        long liveCellCount = gridManager.getGrid().entrySet()
                .stream()
                .filter(e -> e.getValue().isLive())
                .count();
        assertThat(liveCellCount).isEqualTo(1);

        CellState cellWithNeighboursState = gridManager.getGrid().get(cellWithNeighboursPosition);
        assertThat(cellWithNeighboursState.isLive()).isTrue();
        assertThat(cellWithNeighboursState.getColor()).isEqualTo(Color.RED);

        verify(gridEventPublisher, times(0)).publishCellChangedEvent(eq(cellWithNeighboursPosition), any());
    }

    @Test
    public void givenALiveCellWith3Neighbours_whenComputingNextState_thenCellWillSurvive() {
        // given
        CellPosition cellWithNeighboursPosition = new CellPosition(3, 3);
        gridManager.spawnCell(cellWithNeighboursPosition, Color.RED);
        gridManager.spawnCell(new CellPosition(2,2), Color.GREEN);
        gridManager.spawnCell(new CellPosition(4,4), Color.BLUE);
        gridManager.spawnCell(new CellPosition(2,4), Color.BLUE);
        reset(gridEventPublisher);

        // when
        gridManager.nextState();

        // then
        CellState cellWithNeighboursState = gridManager.getGrid().get(cellWithNeighboursPosition);
        assertThat(cellWithNeighboursState.isLive()).isTrue();
        assertThat(cellWithNeighboursState.getColor()).isEqualTo(Color.RED);

        verify(gridEventPublisher, times(0)).publishCellChangedEvent(eq(cellWithNeighboursPosition), any());
    }

    @Test
    public void givenALiveCellWith4Neighbours_whenComputingNextState_thenCellWillDie() {
        // given
        CellPosition cellWithNeighboursPosition = new CellPosition(3, 3);
        gridManager.spawnCell(cellWithNeighboursPosition, Color.RED);
        gridManager.spawnCell(new CellPosition(4,4), Color.BLUE);
        gridManager.spawnCell(new CellPosition(2,2), Color.GREEN);
        gridManager.spawnCell(new CellPosition(2,4), Color.BLUE);
        gridManager.spawnCell(new CellPosition(2,3), Color.BLUE);

        // when
        gridManager.nextState();

        // then
        CellState cellWithNeighboursState = gridManager.getGrid().get(cellWithNeighboursPosition);
        assertThat(cellWithNeighboursState.isLive()).isFalse();
        assertThat(cellWithNeighboursState.getColor()).isEqualTo(null);

        verify(gridEventPublisher, times(1)).publishCellChangedEvent(eq(cellWithNeighboursPosition), eq(CellState.dead()));
    }

    @Test
    public void givenALiveCellWithAllNeighbours_whenComputingNextState_thenCellWillDie() {
        // given
        CellPosition cellWithNeighboursPosition = new CellPosition(3, 3);
        gridManager.spawnCell(cellWithNeighboursPosition, Color.RED);
        gridManager.spawnCell(new CellPosition(4,4), Color.BLUE);
        gridManager.spawnCell(new CellPosition(4,3), Color.BLUE);
        gridManager.spawnCell(new CellPosition(4,2), Color.GREEN);
        gridManager.spawnCell(new CellPosition(2,2), Color.GREEN);
        gridManager.spawnCell(new CellPosition(2,4), Color.BLUE);
        gridManager.spawnCell(new CellPosition(2,3), Color.BLUE);
        gridManager.spawnCell(new CellPosition(3,2), Color.BLUE);
        gridManager.spawnCell(new CellPosition(3,4), Color.BLUE);

        // when
        gridManager.nextState();

        // then
        CellState cellWithNeighboursState = gridManager.getGrid().get(cellWithNeighboursPosition);
        assertThat(cellWithNeighboursState.isLive()).isFalse();
        assertThat(cellWithNeighboursState.getColor()).isEqualTo(null);

        verify(gridEventPublisher, times(1)).publishCellChangedEvent(eq(cellWithNeighboursPosition), eq(CellState.dead()));
    }

    @Test
    public void giveADeadCellWith3Neighbours_whenComputingNextState_thenCellWillSpawn() {
        // given
        gridManager.spawnCell(new CellPosition(2,2), Color.GREEN);
        gridManager.spawnCell(new CellPosition(4,4), Color.BLUE);
        gridManager.spawnCell(new CellPosition(2,4), Color.RED);

        // when
        gridManager.nextState();

        // then
        CellPosition cellWith3NeighboursPosition = new CellPosition(3, 3);
        CellState cellWithNeighboursState = gridManager.getGrid().get(cellWith3NeighboursPosition);
        assertThat(cellWithNeighboursState.isLive()).isTrue();

        Color expectedSpawnedCellColor = new Color(85, 85, 85);
        assertThat(cellWithNeighboursState.getColor()).isEqualTo(expectedSpawnedCellColor);

        verify(gridEventPublisher, times(1)).publishCellChangedEvent(eq(cellWith3NeighboursPosition), eq(CellState.live(expectedSpawnedCellColor)));
    }

    @Test
    public void giveADeadCellWith2Neighbours_whenComputingNextState_thenCellWillRemainDead() {
        // given
        gridManager.spawnCell(new CellPosition(2,2), Color.GREEN);
        gridManager.spawnCell(new CellPosition(4,4), Color.BLUE);
        reset(gridEventPublisher);

        // when
        gridManager.nextState();

        // then
        CellPosition cellWith4NeighboursPosition = new CellPosition(3, 3);
        CellState cellWithNeighboursState = gridManager.getGrid().get(cellWith4NeighboursPosition);
        assertThat(cellWithNeighboursState.isLive()).isFalse();

        verify(gridEventPublisher, times(0)).publishCellChangedEvent(eq(cellWith4NeighboursPosition), any());
    }

    @Test
    public void giveADeadCellWith4Neighbours_whenComputingNextState_thenCellWillRemainDead() {
        // given
        gridManager.spawnCell(new CellPosition(2,2), Color.GREEN);
        gridManager.spawnCell(new CellPosition(4,4), Color.BLUE);
        gridManager.spawnCell(new CellPosition(2,4), Color.RED);
        gridManager.spawnCell(new CellPosition(3,4), Color.RED);
        reset(gridEventPublisher);

        // when
        gridManager.nextState();

        // then
        CellPosition cellWith4NeighboursPosition = new CellPosition(3, 3);
        CellState cellWithNeighboursState = gridManager.getGrid().get(cellWith4NeighboursPosition);
        assertThat(cellWithNeighboursState.isLive()).isFalse();

        verify(gridEventPublisher, times(0)).publishCellChangedEvent(eq(cellWith4NeighboursPosition), any());
    }
}