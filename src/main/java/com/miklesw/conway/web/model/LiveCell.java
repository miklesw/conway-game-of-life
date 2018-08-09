package com.miklesw.conway.web.model;

import java.util.Objects;

public class LiveCell {

    private int x;
    private int y;
    private String color;

    private LiveCell() {
    }

    public LiveCell(int x, int y, String color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LiveCell liveCell = (LiveCell) o;
        return x == liveCell.x &&
                y == liveCell.y &&
                Objects.equals(color, liveCell.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, color);
    }
}
