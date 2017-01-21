package com.evgenltd.mapper.core.entity;

/**
 * Project: mapper
 * Author:  Evgeniy Lebedev
 * Created: 21-01-2017 22:01
 */
public interface Picture extends Identified {
	byte[] getContent();

	void setContent(byte[] content);

	javafx.scene.image.Image getImage();

	void setImage(javafx.scene.image.Image image);
}
