package com.evgenltd.mapper.ui.component.markerediting;

import com.evgenltd.mapper.core.entity.Marker;
import com.evgenltd.mapper.mapviewer.common.Node;
import com.evgenltd.mapper.ui.node.MarkerNode;

import java.util.Optional;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 28-06-2016 10:44
 */
public class MarkerEditing {

	private MarkerNode markerNode;
	private long markerIdentity;
	private long pointIdentity;

	public void setMarker(MarkerNode markerNode) {
		this.markerNode = markerNode;
	}

	public Marker getMarker() {
		return Optional
				.ofNullable(markerNode)
				.map(MarkerNode::getMarker)
				.orElse(null);
	}

	public MarkerNode getMarkerNode()	{
		return markerNode;
	}

	public long getSelectedPointCount()	{
		if(markerNode == null)	{
			return 0;
		}
		return markerNode
				.getChildren()
				.stream()
				.filter(Node::isSelected)
				.count();
	}

	// temp point identity

	public long getNextMarkerIdentity()	{
		return --markerIdentity;
	}

	public long getNextPointIdentity()	{
		return --pointIdentity;
	}

}
