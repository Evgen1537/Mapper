package com.evgenltd.mapper.ui.component.mapviewer;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.bean.Loader;
import com.evgenltd.mapper.core.bean.TrackerBean;
import com.evgenltd.mapper.core.entity.Layer;
import com.evgenltd.mapper.core.entity.Marker;
import com.evgenltd.mapper.core.util.Utils;
import com.evgenltd.mapper.mapviewer.common.ZLevel;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.node.LayerNode;
import com.evgenltd.mapper.ui.node.MarkerNode;
import com.evgenltd.mapper.ui.node.PlayerPositionNode;
import com.evgenltd.mapper.ui.util.UIUtils;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 17-06-2016 01:26
 */
class MapViewerLoaderService extends Service<DataLoadingResult> {

	private final Loader loader = Context.get().getLoader();
	private final TrackerBean trackerBean = Context.get().getTrackerBean();

	private final MapViewerWrapper mapViewerWrapper;

	public MapViewerLoaderService(MapViewerWrapper mapViewer) {
		this.mapViewerWrapper = mapViewer;
	}

	@Override
	protected Task<DataLoadingResult> createTask() {

		final ZLevel level = mapViewerWrapper.getViewportWorldLevel();

		final double worldX = mapViewerWrapper.getViewportWorldX();
		final double worldY = mapViewerWrapper.getViewportWorldY();
		final double worldWidth = mapViewerWrapper.getViewportWorldWidth();
		final double worldHeight = mapViewerWrapper.getViewportWorldHeight();
		final double worldSecondX = worldX + worldWidth;
		final double worldSecondY = worldY + worldHeight;
		final boolean onlyVisibleMarker = mapViewerWrapper.getMarkerOnlyVisibleOption();
		final String markerQuery = mapViewerWrapper.getMarkerSearchQuery();

		return UIUtils.makeTask(() -> backgroundDataLoading(
				worldX,
				worldY,
				worldSecondX,
				worldSecondY,
				level,
				onlyVisibleMarker,
				markerQuery
		));
	}

	@Override
	protected void failed() {
		getException().printStackTrace();
	}

	@Override
	protected void succeeded() {
		foregroundDataApplying(getValue());
	}

	private DataLoadingResult backgroundDataLoading(
			final double worldFirstX,
			final double worldFirstY,
			final double worldSecondX,
			final double worldSecondY,
			final ZLevel level,
			final boolean onlyVisibleMarker,
			final String markerQuery
	)	{

		final double worldTileFirstX = Utils.toTilePosition(worldFirstX);
		final double worldTileFirstY = Utils.toTilePosition(worldFirstY);
		final double worldTileSecondX = Utils.toTilePosition(worldSecondX)+1;
		final double worldTileSecondY = Utils.toTilePosition(worldSecondY)+1;

		UIContext.get().putDebugInfo("BG loading",String.format(
				"%s %s %s %s %s",
				Utils.adjustSizeToLevel(worldTileFirstX, level.getMeasure()),
				Utils.adjustSizeToLevel(worldTileFirstY, level.getMeasure()),
				worldTileSecondX,
				worldTileSecondY,
				level
		));

		final List<Layer> layerList = loader.loadByViewport(
				Utils.adjustSizeToLevel(worldTileFirstX, level.getMeasure()),
				Utils.adjustSizeToLevel(worldTileFirstY, level.getMeasure()),
				worldTileSecondX,
				worldTileSecondY,
				level
		);

		final List<LayerNode> layerNodeList = layerList
				.stream()
				.map(layer -> new LayerNode(layer,level))
				.collect(Collectors.toList());

		final List<Marker> markerList = onlyVisibleMarker
				? loader.loadMarkerListByViewportAndQuery(
						worldFirstX,
						worldFirstY,
						worldSecondX,
						worldSecondY,
						markerQuery
				)
				: loader.loadMarkerListByViewport(
						worldFirstX,
						worldFirstY,
						worldSecondX,
						worldSecondY
				);

		final List<MarkerNode> markerNodeList = markerList
				.stream()
				.map(MarkerNode::new)
				.collect(Collectors.toList());

		final PlayerPositionNode playerPositionNode = trackerBean.getWorldPlayerPosition() != null
				? new PlayerPositionNode(trackerBean.getWorldPlayerPosition())
				: null;

		return new DataLoadingResult(layerNodeList, markerNodeList, playerPositionNode);

	}

	@SuppressWarnings("unchecked")
	private void foregroundDataApplying(final @NotNull DataLoadingResult dataLoadingResult)	{
		mapViewerWrapper.mergeNodes((List)dataLoadingResult.getNodeList());
	}

}
