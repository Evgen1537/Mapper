package com.evgenltd.mapper.core.entity;

/**
 * Project: mapper
 * Author:  Evgeniy Lebedev
 * Created: 21-01-2017 22:24
 */
public interface MarkerIcon extends Identified {

	String getName();
	void setName(String name);

	Picture getImage();
	void setImage(Picture image);

}
