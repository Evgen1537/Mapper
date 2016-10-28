package com.evgenltd.mapper.mapviewer.common;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

/**
 * Project: MapperPrototype
 * Author:  Lebedev
 * Created: 30-December-2015 12:50
 */
class ResizeableCanvasHolder extends Pane{
    private Canvas canvas = new Canvas(100,100);    // dummy

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
        getChildren().clear();
        getChildren().add(canvas);
    }

    @Override
    protected void layoutChildren() {
        if(canvas.getWidth() != getWidth() || canvas.getHeight() != getHeight())    {
            canvas.setWidth(getWidth());
            canvas.setHeight(getHeight());
        }
    }
}
