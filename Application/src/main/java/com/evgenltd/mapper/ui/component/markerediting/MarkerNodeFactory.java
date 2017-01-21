package com.evgenltd.mapper.ui.component.markerediting;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.bean.Loader;
import com.evgenltd.mapper.core.entity.Marker;
import com.evgenltd.mapper.core.entity.impl.EntityFactory;
import com.evgenltd.mapper.core.enums.MarkerType;
import com.evgenltd.mapper.core.util.Constants;
import com.evgenltd.mapper.ui.node.MarkerNode;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 03-07-2016 00:27
 */
public class MarkerNodeFactory {

	public static MarkerNode createPointMarker(final double worldX, final double worldY)	{
		final Loader loader = Context.get().getLoader();

		final Marker marker = EntityFactory.createMarker();
		marker.setType(MarkerType.AREA);
		marker.setMarkerIcon(loader.loadDefaultMarkerIcon());
		marker.setMarkerPointList(Collections.singleton(EntityFactory.createMarkerPoint(worldX, worldY, 1L, marker)));

		return new MarkerNode(marker);
	}

	public static MarkerNode createAreaMarker(final double worldX, final double worldY)	{
		final Loader loader = Context.get().getLoader();

		final Marker marker = EntityFactory.createMarker();
		marker.setType(MarkerType.AREA);
		marker.setMarkerIcon(loader.loadDefaultMarkerIcon());
		marker.setMarkerPointList(new HashSet<>(Arrays.asList(
				EntityFactory.createMarkerPoint(worldX - Constants.TILE_SIZE / 2, worldY - Constants.TILE_SIZE / 2, 1L, marker),
				EntityFactory.createMarkerPoint(worldX + Constants.TILE_SIZE / 2, worldY - Constants.TILE_SIZE / 2, 2L, marker),
				EntityFactory.createMarkerPoint(worldX + Constants.TILE_SIZE / 2, worldY + Constants.TILE_SIZE / 2, 3L, marker),
				EntityFactory.createMarkerPoint(worldX - Constants.TILE_SIZE / 2, worldY + Constants.TILE_SIZE / 2, 4L, marker)
		)));

		return new MarkerNode(marker);
	}

	public static MarkerNode createTrackMarker(final double worldX, final double worldY)	{
		final Marker marker = EntityFactory.createMarker();
		marker.setType(MarkerType.TRACK);
		marker.setMarkerPointList(
				new HashSet<>(Arrays.asList(
						EntityFactory.createMarkerPoint(worldX - Constants.TILE_SIZE / 2, worldY - Constants.TILE_SIZE / 2, 1L, marker),
						EntityFactory.createMarkerPoint(worldX + Constants.TILE_SIZE / 2, worldY + Constants.TILE_SIZE / 2, 2L, marker)
				))
		);

		return new MarkerNode(marker);
	}

	public static MarkerNode createEntranceMarker(final double worldX, final double worldY)	{
		final Loader loader = Context.get().getLoader();

		final Marker marker = EntityFactory.createMarker();
		marker.setType(MarkerType.ENTRANCE);
		marker.setMarkerIcon(loader.loadMarkerIcon(Constants.MARKER_ICON_NAME_CAVE));
		marker.setMarkerPointList(Collections.singleton(EntityFactory.createMarkerPoint(worldX, worldY, 1L, marker)));

		return new MarkerNode(marker);
	}

}
