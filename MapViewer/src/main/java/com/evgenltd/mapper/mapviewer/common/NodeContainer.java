package com.evgenltd.mapper.mapviewer.common;


import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Project: MapperPrototype
 * Author:  Evgeniy
 * Created: 08-06-2016 23:52
 */
class NodeContainer {

	private static Comparator<Node> NODE_COMPARATOR =
			(o1, o2) -> Long.compare(o1.getOrderNumber(), o2.getOrderNumber());

	private final List<Node> allNodes = new ArrayList<>();

	// update nodes

	void mergeNodes(@NotNull final List<Node> nodeList)	{
		NodeMerge.merge(allNodes, nodeList);
		allNodes.sort(NODE_COMPARATOR);
	}

	void addNode(@NotNull final Node node)	{
		allNodes.add(node);
	}

	List<Node> getNodes()	{
		return Collections.unmodifiableList(allNodes);
	}

	// highlight

	boolean updateHighlight(@NotNull final Predicate<Node> condition)	{

		boolean invalidated = false;
		final ListIterator<Node> iterator = allNodes.listIterator(allNodes.size());
		while(iterator.hasPrevious())	{
			final Node node = iterator.previous();
			invalidated = invalidated | node.setHighlighted(condition);
		}

		return invalidated;

	}

	// selection

	boolean updateSelection(@NotNull final Predicate<Node> condition)	{

		boolean invalidated = false;

		final ListIterator<Node> iterator = allNodes.listIterator(allNodes.size());
		while(iterator.hasPrevious())	{
			invalidated = invalidated | iterator.previous().setSelected(condition);
		}

		return invalidated;

	}

	List<Node> getSelectedNodes()	{
		return getSelectedNodes(node -> true);
	}

	List<Node> getSelectedNodes(@NotNull final Predicate<Node> selectedNodeFilter)	{
		return allNodes
				.stream()
				.filter(selectedNodeFilter)
				.filter(Node::isSelected)
				.collect(Collectors.toList());
	}

	boolean isNodeSelected(@NotNull final Predicate<Node> selectedNodeFilter)	{
		return allNodes
				.stream()
				.filter(selectedNodeFilter)
				.anyMatch(Node::isSelected);
	}

	// moving

	boolean updateMoved(@NotNull final Predicate<Node> condition)	{

		boolean invalidated = false;

		for(Node node : allNodes) {
			invalidated = invalidated | node.setMoved(condition);
		}

		return invalidated;

	}

	List<Node> getMovingNodes() {
		return Collections.unmodifiableList(
				allNodes
						.stream()
						.filter(Node::isMoved)
						.collect(Collectors.toList())
		);
	}

	void clearMovingNodes()	{
		allNodes.forEach(node -> node.setMoved(n -> false));
	}

	void moveNodes(final double worldDeltaX, final double worldDeltaY)	{

		allNodes
				.stream()
				.filter(Node::isMoved)
				.forEach(node -> node.move(worldDeltaX, worldDeltaY));

	}

	//

	@Deprecated
	private boolean changeBooleanProperty(
			@NotNull final List<Node> nodeList,
			@NotNull final BiFunction<Node,Boolean,Boolean> setter,
			@NotNull final Boolean newValue
	)	{
		boolean invalidated = false;
		if(nodeList.isEmpty())	{
			return false;
		}
		for(Node node : nodeList) {
			final boolean changed = setter.apply(node,newValue);
			invalidated = invalidated || changed;
		}
		return invalidated;
	}

	@Deprecated
	public Optional<Node> lookupNode(final double worldX, final double worldY)	{
		return allNodes
				.stream()
				.sorted(NODE_COMPARATOR)
				.filter(node -> node.intersect(worldX,worldY))
				.reduce((one,two) -> two);
	}

	@Deprecated
	public Optional<Node> lookupNode(
			final double worldX,
			final double worldY,
			final double worldWidth,
			final double worldHeight
	)	{
		return allNodes
				.stream()
				.sorted(NODE_COMPARATOR)
				.filter(node -> node.intersect(worldX,worldY,worldWidth,worldHeight))
				.reduce((one,two) -> two);
	}

	@Deprecated
	public List<Node> lookupNodes(
			final double worldX,
			final double worldY,
			final double worldWidth,
			final double worldHeight
	)	{
		return allNodes
				.stream()
				.filter(node -> node.intersect(worldX,worldY,worldWidth,worldHeight))
				.collect(Collectors.toList());
	}



	// block

	void lockNodes(@NotNull final Predicate<Node> lockCondition)	{
		allNodes
				.stream()
				.filter(lockCondition)
				.forEach(node -> node.setBlocked(true));
	}

	void lockAllNodes()	{
		allNodes.forEach(node -> node.setBlocked(true));
	}

	void unlockAllNodes()	{
		allNodes.forEach(node -> node.setBlocked(false));
	}

	// util

	@Deprecated
	private static <T> boolean collectionsEquals(@NotNull final List<T> one, @NotNull final List<T> two)	{

		if(one.size() != two.size())	{
			return false;
		}

		final List<T> oneCheck = new ArrayList<>(one);
		final List<T> twoCheck = new ArrayList<>(two);

		oneCheck.removeAll(two);
		twoCheck.removeAll(one);

		return oneCheck.isEmpty() && twoCheck.isEmpty();

	}


}
