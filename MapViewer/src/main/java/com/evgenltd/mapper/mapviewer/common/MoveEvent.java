package com.evgenltd.mapper.mapviewer.common;

/**
 * Project: MapperPrototype
 * Author:  Evgeniy
 * Created: 09-06-2016 03:54
 */
public class MoveEvent extends MapViewerEvent {

	private final double deltaX;
	private final double deltaY;

	public MoveEvent(
			double deltaX,
			double deltaY
	) {
		super(EventProperties.MOVE_EVENT);
		this.deltaX = deltaX;
		this.deltaY = deltaY;
	}

	public double getDeltaX() {
		return deltaX;
	}

	public double getDeltaY() {
		return deltaY;
	}
}
