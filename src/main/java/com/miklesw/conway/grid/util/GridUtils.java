package com.miklesw.conway.grid.util;

import com.miklesw.conway.grid.CellPosition;

import java.util.HashSet;
import java.util.Set;

public final class GridUtils {

    private GridUtils() {}

    public static Set<CellPosition> findNeighbouringCells(CellPosition position, int gridSizeX, int gridSizeY) {
        Set<CellPosition> neighbours = new HashSet<>();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                int neighbourX = position.getX() + x;
                int neighbourY = position.getY() + y;

                if ((x != 0 || y != 0)
                        && (neighbourX >= 1 && neighbourX <= gridSizeX)
                        && (neighbourY >= 1 && neighbourY <= gridSizeY)) {
                    neighbours.add(new CellPosition(neighbourX, neighbourY));
                }
            }
        }
        return neighbours;

    }
}
