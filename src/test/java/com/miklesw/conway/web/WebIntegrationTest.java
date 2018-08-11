package com.miklesw.conway.web;

import com.miklesw.conway.grid.Grid;
import com.miklesw.conway.grid.model.CellPosition;
import com.miklesw.conway.grid.model.CellState;
import com.miklesw.conway.web.model.CellStateChangeInfo;
import com.miklesw.conway.web.model.LiveCell;
import com.miklesw.conway.web.model.Position;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static com.miklesw.conway.utils.ColorUtils.toHexColor;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(
        properties = {
                "grid.next.state.enabled=false"
        }
)
public class WebIntegrationTest {

    @LocalServerPort
    private int port;

    private TestRestTemplate testRestTemplate = new TestRestTemplate();

    private CellChangeEventTestClient cellChangeEventTestClient;

    @Autowired
    private Grid grid;

    @Before
    public void init() {
        ReflectionTestUtils.invokeMethod(grid, "initialize");
        cellChangeEventTestClient = new CellChangeEventTestClient(getBaseUrl());
    }

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api";
    }

    @Test
    public void givenAGridWithNoLiveCells_whenSpawningACell_thenTheCellStateWillBeUpdatedAndAWebEventIsPublished() throws Exception {
        cellChangeEventTestClient.listenToEvents();

        // when
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(getBaseUrl() + "/grid/cells/3/3/spawn", HttpMethod.POST, null, String.class);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);


        Thread.sleep(1000);
        CellState cellState = grid.getCellState(new CellPosition(3, 3));
        assertThat(cellState.isLive()).isTrue();
        assertThat(cellState.getColor()).isNotNull();

        CellStateChangeInfo expectedCellStateChangeEvent = new CellStateChangeInfo(new Position(3, 3), true, toHexColor(cellState.getColor()));

        await().atMost(2, SECONDS)
                .until(() -> cellChangeEventTestClient.receivedEvent(expectedCellStateChangeEvent));
    }

    @Test
    public void givenAGridWithALiveCellForADifferentSession_whenSpawningACellWhichIsAlreadyLive_thenBadRequestStatusWillBeReturnedAndTheCellStateWillNotBeUpdated() throws Exception {

        // given
        testRestTemplate.postForLocation(getBaseUrl() + "/grid/cells/3/3/spawn", null);

        CellState initialCellState = grid.getCellState(new CellPosition(3, 3));

        cellChangeEventTestClient.listenToEvents();

        // when
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(getBaseUrl() + "/grid/cells/3/3/spawn", HttpMethod.POST, null, String.class);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        CellState cellState = grid.getCellState(new CellPosition(3, 3));
        assertThat(cellState.isLive()).isTrue();
        assertThat(cellState.getColor()).isEqualTo(initialCellState.getColor());

        assertThat(cellChangeEventTestClient.getReceivedEvents().size()).isEqualTo(0);
    }

    @Test
    public void givenAGridWithALiveCellForSession_whenSpawningACellWithSameSession_thenheCellStateWillBeUpdatedWithSameColorAsInitialLiveCell() throws Exception {

        // given
        ResponseEntity<String> intitialResponseEntity = testRestTemplate.exchange(getBaseUrl() + "/grid/cells/3/3/spawn", HttpMethod.POST, null, String.class);
        String cookies = intitialResponseEntity.getHeaders().get(HttpHeaders.SET_COOKIE).get(0);

        CellState initialCellState = grid.getCellState(new CellPosition(3, 3));

        // when
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, cookies);

        ResponseEntity<String> responseEntity = testRestTemplate.exchange(getBaseUrl() + "/grid/cells/2/2/spawn", HttpMethod.POST, new HttpEntity<>(null, headers), String.class);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        CellState cellState = grid.getCellState(new CellPosition(2, 2));
        assertThat(cellState.isLive()).isTrue();
        assertThat(cellState.getColor()).isEqualTo(initialCellState.getColor());
    }


    @Test
    public void givenAGridWithLiveCells_whenRetreivingLiveCells_thenLiveCellWillBeReturned() throws Exception {

        // given
        testRestTemplate.exchange(getBaseUrl() + "/grid/cells/3/3/spawn", HttpMethod.POST, null, String.class);
        testRestTemplate.exchange(getBaseUrl() + "/grid/cells/2/1/spawn", HttpMethod.POST, null, String.class);

        ParameterizedTypeReference<List<LiveCell>> parameterizedTypeReference = new ParameterizedTypeReference<List<LiveCell>>() {
        };
        // when
        ResponseEntity<List<LiveCell>> responseEntity = testRestTemplate.exchange(getBaseUrl() + "/grid/cells/live", HttpMethod.GET, null, parameterizedTypeReference);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        CellState cellState3x3 = grid.getCellState(new CellPosition(3, 3));
        CellState cellState2x1 = grid.getCellState(new CellPosition(2, 1));
        List<LiveCell> liveCells = responseEntity.getBody();
        assertThat(liveCells).containsOnly(
                new LiveCell(new Position(3, 3), toHexColor(cellState3x3.getColor())),
                new LiveCell(new Position(2, 1), toHexColor(cellState2x1.getColor()))
        );
    }
}