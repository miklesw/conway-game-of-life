package com.miklesw.conway.web;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * Test the actual game
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(
        properties = {
                "grid.next.state.enabled=true"
        }
)
public class GameOfLiveServiceTest {

    @LocalServerPort
    private int port;

    private CellChangeEventTestClient cellChangeEventTestClient;

    @Test
    public void givenAPATTERN_whenGameTicks_thenPATTERNWILLCHANGEASEXPECTED() {
        // given

        // when

        // then
        Assert.assertTrue(true);

    }
}
