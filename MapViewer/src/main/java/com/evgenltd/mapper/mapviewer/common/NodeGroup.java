package com.evgenltd.mapper.mapviewer.common;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * Project: MapperPrototype
 * Author:  Evgeniy
 * Created: 10-06-2016 00:26
 */
public abstract class NodeGroup extends Node{

	private final List<Node> children = new ArrayList<>();

	public List<Node> getChildren() {
		return Collections.unmodifiableList(children);
	}

	public void add(@NotNull final Node node)	{
		children.add(node);
	}

	public void remove(@NotNull final Node node)    {
		children.remove(node);
	}

	protected void clear()	{
		children.clear();
	}

	@Override
	protected boolean setHighlighted(@NotNull Predicate<Node> changeCondition) {

		boolean invalidated = false;

		if(isEditing())	{
			for(Node child : children) {
				invalidated = invalidated | child.setHighlighted(changeCondition);
			}
		}else {
			invalidated = super.setHighlighted(changeCondition);
			if(invalidated)	{
				for(Node child : children) {
					child.setHighlighted(isHighlighted());
				}
			}

		}

		return invalidated;

	}

	@Override
	protected boolean setSelected(@NotNull Predicate<Node> changeCondition) {

		boolean invalidated = false;

		if(isEditing())	{
			for(Node child : children) {
				invalidated = invalidated | child.setSelected(changeCondition);
			}
		}else {
			invalidated = super.setSelected(changeCondition);
			if(invalidated)	{
				for(Node child : children) {
					child.setSelected(isSelected());
				}
			}
		}

		return invalidated;

	}

	@Override
	protected boolean setMoved(@NotNull Predicate<Node> changeCondition) {

		boolean invalidated = false;

		if(isEditing())	{
			setMoved(true);
			for(Node child : children) {
				invalidated = invalidated | child.setMoved(changeCondition);
			}
		}else {
			invalidated = super.setMoved(changeCondition);
			if(invalidated)	{
				for(Node child : children) {
					child.setMoved(isMoved());
				}
			}
		}

		return invalidated;

	}

	@Override
	protected void setBlocked(boolean blocked) {
		super.setBlocked(blocked);
		for(Node child : children) {
			child.setBlocked(blocked);
		}
	}

	@Override
	protected void setVisible(boolean visible) {
		super.setVisible(visible);
		for(Node child : children) {
			child.setVisible(visible);
		}
	}

	@Override
	public void paint(PaintContext context) {
		for(Node child : children) {
			child.paint(context);
		}
	}

	@Override
	public boolean intersect(double worldX, double worldY) {
		for(Node child : children) {
			if(child.intersect(worldX,worldY))	{
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean intersect(double worldX, double worldY, double worldWidth, double worldHeight) {
		for(Node child : children) {
			if(child.intersect(worldX,worldY,worldWidth,worldHeight))	{
				return true;
			}
		}
		return false;
	}

	@Override
	public void move(double worldDeltaX, double worldDeltaY) {

		children
				.stream()
				.filter(Node::isMoved)
				.forEach(node -> node.move(worldDeltaX, worldDeltaY));

	}

	void mergeChildren(@NotNull final NodeGroup source)	{
		NodeMerge.merge(children,source.children);
		for(Node child : children) {
			child.setParent(this);
		}
	}

}
