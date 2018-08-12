package com.miklesw.conway.web.model;

public class GridSizeResponse {

    private final int x;
    private final int y;

    public GridSizeResponse(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
