package com.evgenltd.mapper.mapviewer.common;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Project: MapperPrototype
 * Author:  Evgeniy
 * Created: 08-06-2016 22:48
 */
public abstract class Node {
	private boolean highlighted;
	private boolean selected;
	private boolean moved;
	private boolean blocked;
	@Deprecated
	private boolean visible = true;
	private boolean editing;
	private long orderNumber;

	private NodeGroup parent;

	public Node() {
	}

	public Node(NodeGroup parent) {
		this.parent = parent;
	}

	// highlight

	public boolean isHighlighted() {
		return highlighted;
	}

	void setHighlighted(boolean highlighted)	{
		this.highlighted = highlighted;
	}

	protected boolean setHighlighted(@NotNull final Predicate<Node> changeCondition) {
		if(moved || blocked || !visible)	{
			return false;
		}
		final boolean changeConditionResult = changeCondition.test(this);
		final boolean invalidated = this.highlighted ^ changeConditionResult;
		this.highlighted = changeConditionResult;
		return invalidated;
	}

	// selection

	public boolean isSelected() {
		return selected;
	}

	void setSelected(boolean selected) {
		this.selected = selected;
	}

	protected boolean setSelected(@NotNull final Predicate<Node> changeCondition)	{
		if(moved || blocked || !visible)	{
			return false;
		}
		final boolean changeConditionResult = changeCondition.test(this);
		final boolean invalidated = this.selected ^ changeConditionResult;
		this.selected = changeConditionResult;
		return invalidated;
	}

	// moved

	public boolean isMoved() {
		return moved;
	}

	void setMoved(boolean moved) {
		this.moved = moved;
	}

	protected boolean setMoved(@NotNull final Predicate<Node> changeCondition)	{
		if(blocked || !visible)	{
			return false;
		}
		final boolean changeConditionResult = changeCondition.test(this);
		final boolean invalidated = this.moved ^ changeConditionResult;
		this.moved = changeConditionResult;
		return invalidated;
	}

	// blocked

	public boolean isBlocked() {
		return blocked;
	}

	protected void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	// visible

	@Deprecated
	public boolean isVisible() {
		return visible;
	}

	@Deprecated
	protected void setVisible(boolean visible) {
		this.visible = visible;
	}

	// editing

	protected void setEditing(final boolean editing) {
		this.editing = editing;
	}

	public boolean isEditing()	{
		return editing;
	}

	// order number

	public long getOrderNumber() {
		return orderNumber;
	}

	protected void setOrderNumber(long orderNumber) {
		this.orderNumber = orderNumber;
	}

	// parent

	public NodeGroup getParent() {
		return parent;
	}

	void setParent(NodeGroup parent) {
		this.parent = parent;
	}

	boolean hasParent()	{
		return parent != null;
	}

	// identifier

	public abstract long getIdentifier();

	// for implementation

	protected abstract void paint(final PaintContext context);

	protected abstract boolean intersect(final double worldX, final double worldY);

	protected abstract boolean intersect(
			final double worldX,
			final double worldY,
			final double worldWidth,
			final double worldHeight
	);

	protected abstract void move(final double worldDeltaX, final double worldDeltaY);

	protected abstract void merge(@NotNull final Node source);

	//

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		Node node = (Node)o;

		return getIdentifier() == node.getIdentifier();

	}

	@Override
	public int hashCode() {
		return (int)(getIdentifier() ^ (getIdentifier() >>> 32));
	}

	enum State	{
		NONE, ADDED, EDITED, DELETED;

		public boolean isModified()	{
			return !Objects.equals(this,NONE);
		}
	}
}
