package com.evgenltd.mapper.core.bean.envers;

import com.evgenltd.mapper.core.entity.Layer;
import com.evgenltd.mapper.core.entity.Marker;
import com.evgenltd.mapper.core.entity.MarkerPoint;
import com.evgenltd.mapper.core.entity.Tile;
import com.evgenltd.mapper.core.entity.envers.*;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 30-07-2016 16:51
 */
public class ChangeFactory {

	public static Aud build(final Object object)	{

		if(object instanceof Layer)	{
			return build((Layer)object);
		}else if(object instanceof Tile)	{
			return build((Tile)object);
		}else if(object instanceof Marker)	{
			return build((Marker)object);
		}else if(object instanceof MarkerPoint)	{
			return build((MarkerPoint)object);
		}else {
			throw new IllegalArgumentException(String.format("Unknown class=[%s]",object.getClass()));
		}

	}

	private static LayerAud build(final Layer layer)	{

		final LayerAud layerAud = new LayerAud();
		layerAud.setId(layer.getId());
		layerAud.setName(layer.getName());
		layerAud.setType(layer.getType());
		layerAud.setX(layer.getX());
		layerAud.setY(layer.getY());
		layerAud.setVisibility(layer.getVisibility());
		layerAud.setOrderNumber(layer.getOrderNumber());
		layerAud.setSessionPath(layer.getSessionPath());
		return layerAud;

	}

	public static TileAud build(final Tile tile)	{

		final TileAud tileAud = new TileAud();
		tileAud.setId(tile.getId());
		tileAud.setX(tile.getX());
		tileAud.setY(tile.getY());
		tileAud.setZ(tile.getZ());
		tileAud.setLayerId(tile.getLayer().getId());
		tileAud.setImageId(tile.getImageId());
		tileAud.setHash(tile.getHash());
		return tileAud;

	}

	public static MarkerAud build(final Marker marker)	{

		final MarkerAud markerAud = new MarkerAud();
		markerAud.setId(marker.getId());
		markerAud.setType(marker.getType());
		markerAud.setMarkerIconId(marker.getMarkerIconId());
		markerAud.setLayerId(marker.getLayerId());
		markerAud.setExitId(marker.getExitId());
		markerAud.setEssence(marker.getEssence());
		markerAud.setSubstance(marker.getSubstance());
		markerAud.setVitality(marker.getVitality());
		markerAud.setComment(marker.getComment());
		return markerAud;

	}

	public static MarkerPointAud build(final MarkerPoint markerPoint)	{

		final MarkerPointAud markerPointAud = new MarkerPointAud();
		markerPointAud.setId(markerPoint.getId());
		markerPointAud.setX(markerPoint.getX());
		markerPointAud.setY(markerPoint.getY());
		markerPointAud.setOrderNumber(markerPoint.getOrderNumber());
		markerPointAud.setMarkerId(markerPoint.getMarker().getId());
		return markerPointAud;

	}
}
