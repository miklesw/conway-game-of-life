package com.miklesw.conway.utils;

import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class ColorUtils {

    private ColorUtils() {
    }

    public static Color average(List<Color> colors) {
        Double redAverage = colors.stream().mapToInt(Color::getRed).average().orElse(0);
        Double greenAverage = colors.stream().mapToInt(Color::getGreen).average().orElse(0);
        Double blueAverage = colors.stream().mapToInt(Color::getBlue).average().orElse(0);

        return new Color(redAverage.intValue(), greenAverage.intValue(), blueAverage.intValue());
    }

    public static String toHexColor(Color color) {
        return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }

    public static Color randomColor() {
        Random rand = new Random();
        int r, g, b;
        r = rand.nextInt(255);
        g = rand.nextInt(255);
        b = rand.nextInt(255);
        return new Color(r, g, b);
    }
}
