package com.evgenltd.mapper.ui.component.mapviewer;

import com.evgenltd.mapper.mapviewer.common.Node;
import com.evgenltd.mapper.ui.node.LayerNode;
import com.evgenltd.mapper.ui.node.MarkerNode;
import com.evgenltd.mapper.ui.node.PlayerPositionNode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 17-06-2016 01:38
 */
class DataLoadingResult {
	private final List<LayerNode> layerNodeList;
	private final List<MarkerNode> markerNodeList;
	private final PlayerPositionNode playerPositionNode;

	public DataLoadingResult(
			@NotNull final List<LayerNode> layerNodeList,
			@NotNull final List<MarkerNode> markerNodeList,
			final PlayerPositionNode playerPositionNode
	) {
		this.layerNodeList = layerNodeList;
		this.markerNodeList = markerNodeList;
		this.playerPositionNode = playerPositionNode;
	}

	public List<LayerNode> getLayerNodeList() {
		return layerNodeList;
	}

	public List<MarkerNode> getMarkerNodeList() {
		return markerNodeList;
	}

	public PlayerPositionNode getPlayerPositionNode() {
		return playerPositionNode;
	}

	public List<Node> getNodeList()	{
		final List<Node> result = new ArrayList<>();
		result.addAll(layerNodeList);
		result.addAll(markerNodeList);
		if(playerPositionNode != null) {
			result.add(playerPositionNode);
		}
		return result;
	}
}
