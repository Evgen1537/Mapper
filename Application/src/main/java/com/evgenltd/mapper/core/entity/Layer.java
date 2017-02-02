package com.evgenltd.mapper.core.entity;

import com.evgenltd.mapper.core.enums.LayerType;
import com.evgenltd.mapper.core.enums.Visibility;

import java.util.Set;

/**
 * Project: mapper
 * Author:  Evgeniy Lebedev
 * Created: 21-01-2017 22:09
 */
public interface Layer extends Identified,Ordered,Movable {

	String getName();
	void setName(String name);

	LayerType getType();
	void setType(LayerType type);

	Double getX();
	void setX(Double x);

	Double getY();
	void setY(Double y);

	Visibility getVisibility();
	void setVisibility(Visibility visibility);

	String getSessionPath();
	void setSessionPath(String sessionPath);

	Boolean getPreventRemove();
	void setPreventRemove(Boolean preventRemove);

	Set<Tile> getTileSet();
	void setTileSet(Set<Tile> tileSet);

}
