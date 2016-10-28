package com.evgenltd.mapper.mapviewer.demo;

import com.evgenltd.mapper.mapviewer.common.Node;
import com.evgenltd.mapper.mapviewer.common.PaintContext;
import com.evgenltd.mapper.mapviewer.util.Geometry;
import javafx.scene.paint.Color;
import math.geom2d.polygon.Rectangle2D;
import org.jetbrains.annotations.NotNull;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 03-07-2016 21:16
 */
public class Block extends Node{

	private long id;
	private Color color;
	private double x;
	private double y;
	private double width;
	private double height;

	public Block(long id, Color color, double x, double y, double width, double height) {
		this.id = id;
		this.color = color;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void setIdentifier(long id)	{
		this.id = id;
	}

	@Override
	public long getIdentifier() {
		return id;
	}

	@Override
	protected void paint(PaintContext context) {

		if(isSelected())	{
			context.rectFill(
					Color.LIGHTGREEN,
					x - 4,
					y - 4,
					width + 8,
					height + 8
			);
		}else if(isHighlighted())	{
			context.rectFill(
					Color.LIGHTBLUE,
					x - 4,
					y - 4,
					width + 8,
					height + 8
			);
		}

		context.rectFill(color, x,y, width,height);

	}

	@Override
	protected boolean intersect(double worldX, double worldY) {
		final Rectangle2D rect = new Rectangle2D(x,y,width,height);
		return rect.contains(worldX, worldY);
	}

	@Override
	protected boolean intersect(
			double worldX,
			double worldY,
			double worldWidth,
			double worldHeight
	) {
		final Rectangle2D blockRect = new Rectangle2D(x,y,width,height);
		final Rectangle2D argumentRect = new Rectangle2D(worldX,worldY,worldWidth,worldHeight);
		return Geometry.intersect(blockRect,argumentRect);
	}

	@Override
	protected void move(double worldDeltaX, double worldDeltaY) {
		x = x + worldDeltaX;
		y = y + worldDeltaY;
	}

	@Override
	protected void merge(@NotNull Node source) {
		final Block block = (Block)source;
		color = block.color;
		x = block.x;
		y = block.y;
		width = block.width;
		height = block.height;
	}

	public Block copy()	{
		return copy(id);
	}

	public Block copy(long id) {
		return new Block(id,color,x,y,width,height);
	}

	@Override
	public String toString() {
		return "Block{" +
				"id=" + id +
				", x=" + x +
				", y=" + y +
				'}';
	}
}
