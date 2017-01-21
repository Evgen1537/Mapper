package com.evgenltd.mapper.core.bean;

import com.evgenltd.mapper.core.entity.*;
import com.evgenltd.mapper.core.entity.dto.LayerDto;
import com.evgenltd.mapper.core.enums.LayerType;
import com.evgenltd.mapper.core.rule.TileInfo;
import com.evgenltd.mapper.mapviewer.common.ZLevel;
import javafx.geometry.Point2D;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Project: mapper
 * Author:  Евгений
 * Created: 04-Сент.-2016 12:16
 */
public interface Loader {

	List<Layer> loadByViewport(
			final Double x1,
			final Double y1,
			final Double x2,
			final Double y2,
			final ZLevel z
	);

	List<LayerDto> loadLayerDtoListByTypes(final LayerType... types);

	List<Layer> loadAllLayerList();

	List<LayerDto> loadSessionLayerList();

	List<LayerDto> loadGlobalLayerList();

	List<Layer> loadLayerListByTypes(final Collection<LayerType> layerTypes);

	math.geom2d.Point2D loadLayerCentroid(final Long layerId);

	List<Point2D> loadTilePoints(final Long layerId, final Tile tile);

	List<Point2D> loadTilePoints(
			final Long layerId,
			final ZLevel level,
			@Nullable final Double x1,
			@Nullable final Double y1,
			@Nullable final Double x2,
			@Nullable final Double y2
	);

	Optional<Tile> loadTile(final TileInfo tileInfo);

	List<Tile> loadTileList(
			Long layerId,
			ZLevel level
	);

	List<? extends Tile> loadLiteTileList(
			Long layerId,
			ZLevel level
	);

	List<Long> loadTileIdList(final Long layerId, final ZLevel level);

	Optional<Tile> loadTile(Long tileId);

	Picture loadImage(final Long imageId);

	// markers

	List<Marker> loadMarkerListByViewport(
			final Double x1,
			final Double y1,
			final Double x2,
			final Double y2
	);

	List<Marker> loadMarkerListByQuery(final String query);

	List<Marker> loadMarkerListByViewportAndQuery(
			final Double x1,
			final Double y1,
			final Double x2,
			final Double y2,
			final String query
	);

	List<Marker> loadMarkerList(final List<Long> markerIdList);

	List<Marker> loadLinkedMarkerList(final List<Long> layerIdList);

	boolean isLayersContainsLinkedMarkers(final List<Long> layerIdList);

	List<MarkerIcon> loadMarkerIconList();

	MarkerIcon loadDefaultMarkerIcon();

	MarkerIcon loadMarkerIcon(final String name);
}
