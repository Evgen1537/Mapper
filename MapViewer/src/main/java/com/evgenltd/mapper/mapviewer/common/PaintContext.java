package com.evgenltd.mapper.mapviewer.common;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.*;
import org.jetbrains.annotations.NotNull;

/**
 * Project: MapperPrototype
 * Author:  Evgeniy
 * Created: 09-06-2016 01:26
 */
public class PaintContext {
	private final GraphicsContext graphicsContext;
	private final Viewport viewport;

	PaintContext(GraphicsContext graphicsContext, Viewport viewport) {
		this.graphicsContext = graphicsContext;
		this.viewport = viewport;
	}

	public GraphicsContext getGraphicsContext() {
		return graphicsContext;
	}

	public double getViewportX() {
		return viewport.getX();
	}

	public double getViewportY() {
		return viewport.getY();
	}

	public double getViewportWidth() {
		return viewport.getWidth();
	}

	public double getViewportHeight() {
		return viewport.getHeight();
	}

	public ZLevel getLevel() {
		return viewport.getLevel();
	}

	public double toCanvasX(double worldX) {
		return viewport.toCanvasX(worldX);
	}

	public double toCanvasY(double worldY) {
		return viewport.toCanvasY(worldY);
	}

	public void setAlpha(final double alpha)  {
		graphicsContext.setGlobalAlpha(alpha);
	}

	public void lineStroke(
			@NotNull final Color color,
			final double strokeWidth,
			final double worldX1,
			final double worldY1,
			final double worldX2,
			final double worldY2
	)	{
		graphicsContext.setStroke(color);
		graphicsContext.setLineWidth(strokeWidth);
		graphicsContext.strokeLine(
				viewport.toCanvasX(worldX1),
				viewport.toCanvasY(worldY1),
				viewport.toCanvasX(worldX2),
				viewport.toCanvasY(worldY2)
		);
	}

	public void lineStrokeDashed(
			@NotNull final Color color,
			final double strokeWidth,
			final double worldX1,
			final double worldY1,
			final double worldX2,
			final double worldY2,
	        final double... lineDashes
	)  {
		graphicsContext.setLineDashes(lineDashes);
		lineStroke(color, strokeWidth, worldX1, worldY1, worldX2, worldY2);
		graphicsContext.setLineDashes();
	}

	public void rectFill(
			@NotNull final Color color,
			final double worldX,
			final double worldY,
			final double worldWidth,
			final double worldHeight
	) {
		graphicsContext.setFill(color);
		graphicsContext.fillRect(
				viewport.toCanvasX(worldX),
				viewport.toCanvasY(worldY),
				viewport.toCanvasSize(worldWidth),
				viewport.toCanvasSize(worldHeight)
		);
	}

	public void ovalGradientLeveled(
			@NotNull final Color centerColor,
			@NotNull final Color outerColor,
			final double worldX,
			final double worldY,
			final double width,
			final double height
	)	{
		ovalGradient(
				centerColor,
				outerColor,
				worldX,
				worldY,
				viewport.toCanvasSize(width),
				viewport.toCanvasSize(height)
		);
	}

	public void ovalGradient(
			@NotNull final Color centerColor,
			@NotNull final Color outerColor,
			final double worldX,
			final double worldY,
			final double width,
			final double height
	)	{
		ovalFill(
				getRadialGradient(centerColor, outerColor),
				worldX,
				worldY,
				width,
				height
		);
	}

	public void ovalFill(
			@NotNull final Paint paint,
			final double worldX,
			final double worldY,
			final double width,
			final double height
	)	{
		final double canvasX = viewport.toCanvasX(worldX);
		final double canvasY = viewport.toCanvasY(worldY);

		final double canvasCornerX = canvasX - width / 2;
		final double canvasCornerY = canvasY - height / 2;

		graphicsContext.setFill(paint);
		graphicsContext.fillOval(
				canvasCornerX,
				canvasCornerY,
				width,
				height
		);
	}

	public void ovalStroke(
			@NotNull final Paint paint,
			final double strokeWidth,
			final double worldX,
			final double worldY,
			final double width,
			final double height
	)	{
		final double canvasX = viewport.toCanvasX(worldX);
		final double canvasY = viewport.toCanvasY(worldY);

		final double canvasCornerX = canvasX - width / 2;
		final double canvasCornerY = canvasY - height / 2;

		graphicsContext.setStroke(paint);
		graphicsContext.setLineWidth(strokeWidth);
		graphicsContext.strokeOval(
				canvasCornerX,
				canvasCornerY,
				width,
				height
		);
	}

	public void image(
			@NotNull final Image image,
			final double worldX,
			final double worldY
	) {
		graphicsContext.drawImage(
				image,
				viewport.toCanvasX(worldX),
				viewport.toCanvasY(worldY)
		);
	}

	public void imageLeveled(
			@NotNull final Image image,
			final double worldX,
			final double worldY,
			final double imageWidth,
			final double imageHeight
	) {

		final double imageWidthScaled = viewport.toCanvasSize(imageWidth);
		final double imageHeightScaled = viewport.toCanvasSize(imageHeight);

		final double canvasCenterX = viewport.toCanvasX(worldX);
		final double canvasCenterY = viewport.toCanvasY(worldY);
		final double canvasCornerX = canvasCenterX - imageWidthScaled / 2;
		final double canvasCornerY = canvasCenterY - imageHeightScaled / 2;

		graphicsContext.drawImage(
				image,
				canvasCornerX,
				canvasCornerY,
				imageWidthScaled,
				imageHeightScaled
		);

	}

	public void polygonFill(
			@NotNull final Color color,
			double alpha,
			double[] xWorldPoints,
			double[] yWorldPoints,
			int nPoints
	) {
		final double[] xCanvasPoints = viewport.toCanvasX(xWorldPoints);
		final double[] yCanvasPoints = viewport.toCanvasY(yWorldPoints);

		setAlpha(alpha);
		graphicsContext.setFill(color);
		graphicsContext.fillPolygon(xCanvasPoints, yCanvasPoints, nPoints);
		setAlpha(1);

	}

	public void polygonStroke(
			@NotNull final Color color,
			double strokeWidth,
			double[] xWorldPoints,
			double[] yWorldPoints,
			int nPoints
	)	{
		final double[] xCanvasPoints = viewport.toCanvasX(xWorldPoints);
		final double[] yCanvasPoints = viewport.toCanvasY(yWorldPoints);

		graphicsContext.setStroke(color);
		graphicsContext.setLineWidth(strokeWidth);
		graphicsContext.strokePolygon(xCanvasPoints, yCanvasPoints, nPoints);
		graphicsContext.setLineWidth(1);
	}

	public void polylineStroke(
			@NotNull final Color color,
			double width,
			double[] xWorldPoints,
			double[] yWorldPoints,
			int nPoints
	) {
		final double[] xCanvasPoints = viewport.toCanvasX(xWorldPoints);
		final double[] yCanvasPoints = viewport.toCanvasY(yWorldPoints);

		graphicsContext.setLineWidth(width);
		graphicsContext.setStroke(color);
		graphicsContext.strokePolyline(xCanvasPoints, yCanvasPoints, nPoints);
		graphicsContext.setLineWidth(1);
	}

	private Paint getRadialGradient(@NotNull final Color center, @NotNull final Color outer)	{
		return new RadialGradient(
				0,
				0,
				0.5,
				0.5,
				0.6,
				true,
				CycleMethod.NO_CYCLE,
				new Stop(0, center),
				new Stop(1, outer)
		);
	}
}
