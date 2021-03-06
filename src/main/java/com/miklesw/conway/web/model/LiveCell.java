package com.miklesw.conway.web.model;

import java.util.Objects;

public class LiveCell {

    private Position position;
    private String color;

    private LiveCell() {
    }

    public LiveCell(Position position, String color) {
        this.position = position;
        this.color = color;
    }

    public Position getPosition() {
        return position;
    }

    public String getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LiveCell liveCell = (LiveCell) o;
        return Objects.equals(position, liveCell.position) &&
                Objects.equals(color, liveCell.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, color);
    }
}
