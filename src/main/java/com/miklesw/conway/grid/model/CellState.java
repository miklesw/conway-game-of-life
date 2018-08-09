package com.miklesw.conway.grid.model;

import org.springframework.util.Assert;

import java.awt.Color;
import java.util.Objects;

public class CellState {

    private final boolean live;
    private final Color color ;

    private CellState(boolean live, Color color) {
        this.live = live;
        this.color = color;
    }

    public static CellState live(Color color) {
        Assert.notNull(color, "Live cells must be assigned a color");
        return new CellState(true, color);
    }

    public static CellState dead() {
        return new CellState(false, null);
    }

    public boolean isLive(){
        return live;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CellState cellState = (CellState) o;
        return live == cellState.live &&
                Objects.equals(color, cellState.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(live, color);
    }

    @Override
    public String toString() {
        return "CellState{" +
                "live=" + live +
                ", session=" + color +
                '}';
    }
}
