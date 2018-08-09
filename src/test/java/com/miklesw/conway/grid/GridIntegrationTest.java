package com.miklesw.conway.grid;

import com.miklesw.conway.grid.events.GridEventPublisher;
import com.miklesw.conway.grid.model.CellPosition;
import com.miklesw.conway.grid.model.CellState;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.awt.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {GridIntegrationTest.TestGridConfiguration.class})
@TestPropertySource(
        properties = {
                "grid.size.x=6",
                "grid.size.y=6",
                "grid.impl=memory"
        }
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class GridIntegrationTest {

    @Autowired
    private Grid grid;
    
    @Autowired
    private GridService gridService;

    @Autowired
    private GridEventPublisher gridEventPublisher;

    @Before
    public void init() {
        reset(gridEventPublisher);
    }

    @Test
    public void givenADeadCell_whenSpawningACell_thenCellStateAndColorWillBeUpdated() {
        // given
        CellPosition position = new CellPosition(3, 3);
        Color green = Color.GREEN;

        // when
        gridService.spawnCell(position, green);

        // then
        CellState updatedCellState = grid.getCells().get(position);
        assertThat(updatedCellState.isLive()).isTrue();
        assertThat(updatedCellState.getColor()).isEqualTo(green);
    }

    @Test
    public void givenALiveCell_whenSpawningACell_WillThrowAnIllegalStateException() {
        // given
        CellPosition position = new CellPosition(3, 3);

        Color initialColor = Color.RED;
        gridService.spawnCell(position, initialColor);

        Color color = Color.GREEN;

        // when
        Throwable thrown = catchThrowable(() -> gridService.spawnCell(position, color));

        // when
        assertThat(thrown).isInstanceOf(IllegalStateException.class)
                .hasMessage("Cell at %s is already live with colour %s!", position, initialColor);

        CellState updatedCellState = grid.getCells().get(position);
        assertThat(updatedCellState.isLive()).isTrue();
        assertThat(updatedCellState.getColor()).isEqualTo(initialColor);

    }

    @Test
    public void givenALiveCellWithNoNeighbours_whenComputingNextState_thenCellWillDie() {
        // given
        CellPosition cellPosition = new CellPosition(3, 3);
        gridService.spawnCell(cellPosition, Color.RED);

        // when
        gridService.computeNextState();

        // then
        long liveCellCount = grid.getCells().entrySet()
                .stream()
                .filter(e -> e.getValue().isLive())
                .count();
        assertThat(liveCellCount).isEqualTo(0);

        CellState cellWithNeighboursState = grid.getCells().get(cellPosition);
        assertThat(cellWithNeighboursState.isLive()).isFalse();
        assertThat(cellWithNeighboursState.getColor()).isEqualTo(null);

        verify(gridEventPublisher, times(1)).publishCellChangedEvent(eq(cellPosition), eq(CellState.dead()));
    }

    @Test
    public void givenALiveCellWith1Neighbour_whenComputingNextState_thenCellWillDie() {
        // given
        CellPosition cellWithNeighboursPosition = new CellPosition(3, 3);
        gridService.spawnCell(cellWithNeighboursPosition, Color.RED);
        gridService.spawnCell(new CellPosition(2, 3), Color.RED);

        // when
        gridService.computeNextState();

        // then
        long liveCellCount = grid.getCells().entrySet()
                .stream()
                .filter(e -> e.getValue().isLive())
                .count();
        assertThat(liveCellCount).isEqualTo(0);

        CellState cellWithNeighboursState = grid.getCells().get(cellWithNeighboursPosition);
        assertThat(cellWithNeighboursState.isLive()).isFalse();
        assertThat(cellWithNeighboursState.getColor()).isNull();

        verify(gridEventPublisher, times(1)).publishCellChangedEvent(eq(cellWithNeighboursPosition), eq(CellState.dead()));
    }


    @Test
    public void givenALiveCellWith2Neighbours_whenComputingNextState_thenCellWillSurvive() {
        // given
        CellPosition cellWithNeighboursPosition = new CellPosition(3, 3);
        gridService.spawnCell(cellWithNeighboursPosition, Color.RED);
        gridService.spawnCell(new CellPosition(2, 2), Color.GREEN);
        gridService.spawnCell(new CellPosition(4, 4), Color.BLUE);
        reset(gridEventPublisher);

        // when
        gridService.computeNextState();

        // then
        long liveCellCount = grid.getCells().entrySet()
                .stream()
                .filter(e -> e.getValue().isLive())
                .count();
        assertThat(liveCellCount).isEqualTo(1);

        CellState cellWithNeighboursState = grid.getCells().get(cellWithNeighboursPosition);
        assertThat(cellWithNeighboursState.isLive()).isTrue();
        assertThat(cellWithNeighboursState.getColor()).isEqualTo(Color.RED);

        verify(gridEventPublisher, times(0)).publishCellChangedEvent(eq(cellWithNeighboursPosition), any());
    }

    @Test
    public void givenALiveCellWith3Neighbours_whenComputingNextState_thenCellWillSurvive() {
        // given
        CellPosition cellWithNeighboursPosition = new CellPosition(3, 3);
        gridService.spawnCell(cellWithNeighboursPosition, Color.RED);
        gridService.spawnCell(new CellPosition(2, 2), Color.GREEN);
        gridService.spawnCell(new CellPosition(4, 4), Color.BLUE);
        gridService.spawnCell(new CellPosition(2, 4), Color.BLUE);
        reset(gridEventPublisher);

        // when
        gridService.computeNextState();

        // then
        CellState cellWithNeighboursState = grid.getCells().get(cellWithNeighboursPosition);
        assertThat(cellWithNeighboursState.isLive()).isTrue();
        assertThat(cellWithNeighboursState.getColor()).isEqualTo(Color.RED);

        verify(gridEventPublisher, times(0)).publishCellChangedEvent(eq(cellWithNeighboursPosition), any());
    }

    @Test
    public void givenALiveCellWith4Neighbours_whenComputingNextState_thenCellWillDie() {
        // given
        CellPosition cellWithNeighboursPosition = new CellPosition(3, 3);
        gridService.spawnCell(cellWithNeighboursPosition, Color.RED);
        gridService.spawnCell(new CellPosition(4, 4), Color.BLUE);
        gridService.spawnCell(new CellPosition(2, 2), Color.GREEN);
        gridService.spawnCell(new CellPosition(2, 4), Color.BLUE);
        gridService.spawnCell(new CellPosition(2, 3), Color.BLUE);

        // when
        gridService.computeNextState();

        // then
        CellState cellWithNeighboursState = grid.getCells().get(cellWithNeighboursPosition);
        assertThat(cellWithNeighboursState.isLive()).isFalse();
        assertThat(cellWithNeighboursState.getColor()).isEqualTo(null);

        verify(gridEventPublisher, times(1)).publishCellChangedEvent(eq(cellWithNeighboursPosition), eq(CellState.dead()));
    }

    @Test
    public void givenALiveCellWithAllNeighbours_whenComputingNextState_thenCellWillDie() {
        // given
        CellPosition cellWithNeighboursPosition = new CellPosition(3, 3);
        gridService.spawnCell(cellWithNeighboursPosition, Color.RED);
        gridService.spawnCell(new CellPosition(4, 4), Color.BLUE);
        gridService.spawnCell(new CellPosition(4, 3), Color.BLUE);
        gridService.spawnCell(new CellPosition(4, 2), Color.GREEN);
        gridService.spawnCell(new CellPosition(2, 2), Color.GREEN);
        gridService.spawnCell(new CellPosition(2, 4), Color.BLUE);
        gridService.spawnCell(new CellPosition(2, 3), Color.BLUE);
        gridService.spawnCell(new CellPosition(3, 2), Color.BLUE);
        gridService.spawnCell(new CellPosition(3, 4), Color.BLUE);

        // when
        gridService.computeNextState();

        // then
        CellState cellWithNeighboursState = grid.getCells().get(cellWithNeighboursPosition);
        assertThat(cellWithNeighboursState.isLive()).isFalse();
        assertThat(cellWithNeighboursState.getColor()).isEqualTo(null);

        verify(gridEventPublisher, times(1)).publishCellChangedEvent(eq(cellWithNeighboursPosition), eq(CellState.dead()));
    }

    @Test
    public void giveADeadCellWith3Neighbours_whenComputingNextState_thenCellWillSpawn() {
        // given
        gridService.spawnCell(new CellPosition(2, 2), Color.GREEN);
        gridService.spawnCell(new CellPosition(4, 4), Color.BLUE);
        gridService.spawnCell(new CellPosition(2, 4), Color.RED);

        // when
        gridService.computeNextState();

        // then
        CellPosition cellWith3NeighboursPosition = new CellPosition(3, 3);
        CellState cellWithNeighboursState = grid.getCells().get(cellWith3NeighboursPosition);
        assertThat(cellWithNeighboursState.isLive()).isTrue();

        Color expectedSpawnedCellColor = new Color(85, 85, 85);
        assertThat(cellWithNeighboursState.getColor()).isEqualTo(expectedSpawnedCellColor);

        verify(gridEventPublisher, times(1)).publishCellChangedEvent(eq(cellWith3NeighboursPosition), eq(CellState.live(expectedSpawnedCellColor)));
    }

    @Test
    public void giveADeadCellWith2Neighbours_whenComputingNextState_thenCellWillRemainDead() {
        // given
        gridService.spawnCell(new CellPosition(2, 2), Color.GREEN);
        gridService.spawnCell(new CellPosition(4, 4), Color.BLUE);
        reset(gridEventPublisher);

        // when
        gridService.computeNextState();

        // then
        CellPosition cellWith4NeighboursPosition = new CellPosition(3, 3);
        CellState cellWithNeighboursState = grid.getCells().get(cellWith4NeighboursPosition);
        assertThat(cellWithNeighboursState.isLive()).isFalse();

        verify(gridEventPublisher, times(0)).publishCellChangedEvent(eq(cellWith4NeighboursPosition), any());
    }

    @Test
    public void giveADeadCellWith4Neighbours_whenComputingNextState_thenCellWillRemainDead() {
        // given
        gridService.spawnCell(new CellPosition(2, 2), Color.GREEN);
        gridService.spawnCell(new CellPosition(4, 4), Color.BLUE);
        gridService.spawnCell(new CellPosition(2, 4), Color.RED);
        gridService.spawnCell(new CellPosition(3, 4), Color.RED);
        reset(gridEventPublisher);

        // when
        gridService.computeNextState();

        // then
        CellPosition cellWith4NeighboursPosition = new CellPosition(3, 3);
        CellState cellWithNeighboursState = grid.getCells().get(cellWith4NeighboursPosition);
        assertThat(cellWithNeighboursState.isLive()).isFalse();

        verify(gridEventPublisher, times(0)).publishCellChangedEvent(eq(cellWith4NeighboursPosition), any());
    }

    @Configuration
    @Import(GridConfig.class)
    public static class TestGridConfiguration {

        @Bean
        GridEventPublisher gridEventPublisher() {
            return mock(GridEventPublisher.class);
        }
    }
}