package com.miklesw.conway.web.model;

import java.util.Objects;

public class CellStateChangeInfo {

    private Position position;
    private boolean live;
    private String color;

    private CellStateChangeInfo() {
    }

    public CellStateChangeInfo(Position position, boolean live, String color) {
        this.position = position;
        this.live = live;
        this.color = color;
    }

    public Position getPosition() {
        return position;
    }

    public boolean isLive() {
        return live;
    }

    public String getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CellStateChangeInfo that = (CellStateChangeInfo) o;
        return live == that.live &&
                Objects.equals(position, that.position) &&
                Objects.equals(color, that.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, live, color);
    }
}
