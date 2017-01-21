package com.evgenltd.mapper.core.entity.impl;

import com.evgenltd.mapper.core.entity.*;
import com.evgenltd.mapper.core.enums.LayerType;
import com.evgenltd.mapper.core.enums.Visibility;
import javafx.scene.image.Image;

import java.util.HashSet;

/**
 * Project: mapper
 * Author:  Evgeniy Lebedev
 * Created: 21-01-2017 21:51
 */
public class EntityFactory {

	public static FolderEntry createFolderEntry() {
		return new FolderEntryImpl();
	}

	public static Picture createPicture() {
		return new PictureImpl();
	}

	public static Layer createLayer() {
		final Layer layer = new LayerImpl();
		layer.setType(LayerType.SESSION);
		layer.setX(0D);
		layer.setY(0D);
		layer.setTileSet(new HashSet<>());
		layer.setVisibility(Visibility.FULL);
		return layer;
	}

	public static MarkerIcon createMarkerIcon() {
		return new MarkerIconImpl();
	}

	public static MarkerIcon createMarkerIcon(final String name, final Image image) {
		final Picture picture = createPicture();
		picture.setImage(image);

		final MarkerIcon markerIcon = createMarkerIcon();
		markerIcon.setName(name);
		markerIcon.setImage(picture);
		return markerIcon;
	}

	public static Marker createMarker() {
		return new MarkerImpl();
	}

	public static MarkerPoint createMarkerPoint() {
		return new MarkerPointImpl();
	}

	public static MarkerPoint createMarkerPoint(final Double x, final Double y, final Long orderNumber, final Marker marker) {
		final MarkerPoint markerPoint = createMarkerPoint();
		markerPoint.setX(x);
		markerPoint.setY(y);
		markerPoint.setOrderNumber(orderNumber);
		markerPoint.setMarker(marker);
		return markerPoint;
	}

	public static Tile createTile() {
		return new TileImpl();
	}

}
