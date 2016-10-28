package com.evgenltd.mapper.mapviewer.common;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 01-07-2016 01:16
 */
class NodeMerge {

	/**
	 * Performs merging of two specified list with nodes
	 * Please keep in mind that first list will be changed during invocation
	 *
	 * @param target target list, which receive all changes
	 * @param source source list, which content will be merged with target list
	 */
	static void merge(@NotNull List<Node> target, @NotNull final List<Node> source)	{

		List<Node> sourceTemp = new ArrayList<>(source);

		updateTargetList(target, sourceTemp);
		fillUpTargetWithOtherSource(target, sourceTemp);

	}

	private static void updateTargetList(@NotNull List<Node> target, @NotNull List<Node> source)	{

		final Iterator<Node> targetIterator = target.iterator();

		while(targetIterator.hasNext()) {

			final Node targetNode = targetIterator.next();
			final int sourceNodeIndex = source.indexOf(targetNode);
			final boolean isTargetNodeNotExistsInSource = sourceNodeIndex < 0;

			if(isTargetNodeNotExistsInSource) {
				processDeletionTargetNode(targetNode, targetIterator);
			}else {
				processMergeTargetNode(targetNode, source.remove(sourceNodeIndex));
			}
		}

	}

	private static void processDeletionTargetNode(@NotNull final Node targetNode, @NotNull Iterator<Node> targetIterator)	{
		if(targetNode.isEditing()) {
			return;
		}
		targetIterator.remove();
	}

	private static void processMergeTargetNode(@NotNull Node targetNode, @NotNull final Node sourceNode) {

		if(targetNode.isEditing()) {
			return;
		}

		targetNode.merge(sourceNode);

		if(targetNode instanceof NodeGroup)	{
			final NodeGroup targetNodeGroup = (NodeGroup)targetNode;
			final NodeGroup sourceNodeGroup = (NodeGroup)sourceNode;
			targetNodeGroup.mergeChildren(sourceNodeGroup);
		}

	}

	private static void fillUpTargetWithOtherSource(@NotNull List<Node> target, final @NotNull List<Node> source)	{
		target.addAll(source);
	}

}
