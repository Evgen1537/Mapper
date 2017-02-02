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
			Double x1,
			Double y1,
			Double x2,
			Double y2,
			ZLevel z
	);

	List<LayerDto> loadLayerDtoListByTypes(LayerType... types);

	List<Layer> loadAllLayerList();

	List<LayerDto> loadAllLayerDtoList();

	List<Layer> loadLayerListByTypes(Collection<LayerType> layerTypes);
	
	Layer loadLayer(Long id);

	math.geom2d.Point2D loadLayerCentroid(Long layerId);

	List<Point2D> loadTilePoints(Long layerId, Tile tile);

	List<Point2D> loadTilePoints(
			Long layerId,
			ZLevel level,
			@Nullable Double x1,
			@Nullable Double y1,
			@Nullable Double x2,
			@Nullable Double y2
	);

	Optional<Tile> loadTile(TileInfo tileInfo);

	List<Tile> loadTileList(
			Long layerId,
			ZLevel level
	);

	List<? extends Tile> loadLiteTileList(
			Long layerId,
			ZLevel level
	);

	List<Long> loadTileIdList(Long layerId, ZLevel level);

	Optional<Tile> loadTile(Long tileId);

	Picture loadImage(Long imageId);

	// markers

	List<Marker> loadMarkerListByViewport(
			Double x1,
			Double y1,
			Double x2,
			Double y2
	);

	List<Marker> loadMarkerListByQuery(String query);

	List<Marker> loadMarkerListByViewportAndQuery(
			Double x1,
			Double y1,
			Double x2,
			Double y2,
			String query
	);

	List<Marker> loadMarkerList(List<Long> markerIdList);

	List<Marker> loadLinkedMarkerList(List<Long> layerIdList);

	boolean isLayersContainsLinkedMarkers(List<Long> layerIdList);

	List<MarkerIcon> loadMarkerIconList();

	MarkerIcon loadDefaultMarkerIcon();

	MarkerIcon loadMarkerIcon(String name);
}
