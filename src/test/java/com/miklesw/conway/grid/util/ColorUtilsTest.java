package com.miklesw.conway.grid.util;

import com.miklesw.conway.utils.ColorUtils;
import org.junit.Test;

import java.awt.*;
import java.util.List;

import static java.awt.Color.*;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;


public class ColorUtilsTest {

    @Test
    public void givenAListOfUniqueColours_whenAverageMethodIsCalled_thenCorrectAverageColorShouldBeReturned(){
        // given
        List<Color> colors = asList(BLUE, YELLOW, GREEN, new Color(200,50,50));

        // when
        Color result = ColorUtils.average(colors);

        // then
        assertThat(result).isEqualTo(new Color(113,140,76));
    }

    @Test
    public void givenAListOfNonUniqueColours_whenAverageMethodIsCalled_thenCorrectAverageColorShouldBeReturned(){
        // given
        List<Color> colors = asList(BLUE, YELLOW, YELLOW, GREEN, GREEN, new Color(200,50,50));

        // when
        Color result = ColorUtils.average(colors);

        // then
        assertThat(result).isEqualTo(new Color(118,178,50));
    }

}