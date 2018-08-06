package com.miklesw.conway.utils;

import java.awt.*;
import java.util.List;

public final class ColorUtils {

    private ColorUtils(){}

    public static Color average(List<Color> colors) {
        Double redAverage = colors.stream().mapToInt(Color::getRed).average().orElse(0);
        Double greenAverage = colors.stream().mapToInt(Color::getGreen).average().orElse(0);
        Double blueAverage = colors.stream().mapToInt(Color::getBlue).average().orElse(0);

        return new Color(redAverage.intValue(), greenAverage.intValue(), blueAverage.intValue());
    }


}
