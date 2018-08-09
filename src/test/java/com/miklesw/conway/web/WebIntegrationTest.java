package com.miklesw.conway.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miklesw.conway.grid.Grid;
import com.miklesw.conway.grid.model.CellPosition;
import com.miklesw.conway.grid.model.CellState;
import com.miklesw.conway.web.model.LiveCell;
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
import java.util.stream.Collectors;

import static com.miklesw.conway.utils.ColorUtils.toHexColor;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(
        properties = {
                "grid.size.x=10",
                "grid.size.y=10",
                "grid.impl=memory",
                "session.color.repo.impl=memory"
        }
)
// TODO: disable scheduling
public class WebIntegrationTest {

    @LocalServerPort
    private int port;

    private TestRestTemplate testRestTemplate = new TestRestTemplate();

    @Autowired
    private Grid grid;

    @Before
    public void init() {
        ReflectionTestUtils.invokeMethod(grid, "initialize");
    }

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    public void givenAGridWithNoLiveCells_whenSpawningACell_thenTheCellStateWillBeUpdated() throws Exception {

        // when
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(getBaseUrl() + "/grid/cells/3/3/spawn", HttpMethod.POST, null, String.class);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        CellState cellState = grid.getCellState(new CellPosition(3, 3));
        assertThat(cellState.isLive()).isTrue();
        assertThat(cellState.getColor()).isNotNull();

        // TODO test for event via web socket
        // TODO test for new session for different user/agent
    }

    @Test
    public void givenAGridWithALiveCellForADifferentSession_whenSpawningACellWhichIsAlreadyLive_thenBadRequestStatusWillBeReturnedAndTheCellStateWillNotBeUpdated() throws Exception {

        // given
        testRestTemplate.postForLocation(getBaseUrl() + "/grid/cells/3/3/spawn", null);

        CellState initialCellState = grid.getCellState(new CellPosition(3, 3));

        // when
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(getBaseUrl() + "/grid/cells/3/3/spawn", HttpMethod.POST, null, String.class);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        CellState cellState = grid.getCellState(new CellPosition(3, 3));
        assertThat(cellState.isLive()).isTrue();
        assertThat(cellState.getColor()).isEqualTo(initialCellState.getColor());
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
                new LiveCell(3, 3, toHexColor(cellState3x3.getColor())),
                new LiveCell(2, 1, toHexColor(cellState2x1.getColor()))
        );
    }
}