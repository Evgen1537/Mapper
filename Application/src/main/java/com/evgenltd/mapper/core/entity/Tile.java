package com.evgenltd.mapper.core.entity;

import com.evgenltd.mapper.mapviewer.common.ZLevel;
import javafx.geometry.Point2D;

import java.util.List;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 13-06-2016 01:13
 */
public interface Tile {

	Long getId();
	void setId(Long id);

	Double getX();
	void setX(Double x);

	Double getY();
	void setY(Double y);

	ZLevel getZ();
	void setZ(ZLevel z);

	Layer getLayer();
	void setLayer(Layer layer);

	byte[] getContent();

	Long getImageId();
	Image getImageEntity();

	javafx.scene.image.Image getImage();
	void setImage(javafx.scene.image.Image image);

	String getHash();
	void setHash(String hash);

	List<Point2D> getLowLevelTilePointList();
	void setLowLevelTilePointList(List<Point2D> lowLevelTilePointList);
}