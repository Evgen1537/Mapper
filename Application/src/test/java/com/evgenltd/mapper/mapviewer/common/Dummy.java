package com.evgenltd.mapper.mapviewer.common;

import org.jetbrains.annotations.NotNull;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 01-07-2016 03:01
 */
class Dummy extends Node {

	private Long id;
	private boolean innerState;

	public Dummy(Long id) {
		this.id = id;
	}

	public boolean isInnerState() {
		return innerState;
	}

	public void setInnerState(boolean innerState) {
		this.innerState = innerState;
	}

	@Override
	public long getIdentifier() {
		return id;
	}

	@Override
	public void paint(PaintContext context) {
	}

	@Override
	public boolean intersect(double worldX, double worldY) {
		return false;
	}

	@Override
	public boolean intersect(
			double worldX,
			double worldY,
			double worldWidth,
			double worldHeight
	) {
		return false;
	}

	@Override
	public void move(double worldDeltaX, double worldDeltaY) {}

	@Override
	public void merge(@NotNull final Node source) {
		final Dummy sourceDummy = (Dummy)source;
		innerState = sourceDummy.innerState;
	}

	@Override
	public String toString() {
		return "Dummy{" +
				"id=" + id +
				'}';
	}

	public static Dummy make(@NotNull final Long id)	{
		return make(id, false);
	}

	public static Dummy make(@NotNull final Long id, final boolean innerState)	{
		return make(id, innerState, false);
	}

	public static Dummy make(@NotNull final Long id, final boolean innerState, final boolean editing)	{
		Dummy dummy = new Dummy(id);
		dummy.setInnerState(innerState);
		dummy.setEditing(editing);
		return dummy;
	}
}
