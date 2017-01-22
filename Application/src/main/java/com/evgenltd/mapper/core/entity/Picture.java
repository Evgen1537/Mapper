package com.evgenltd.mapper.core.entity;

import javafx.scene.image.Image;

/**
 * Project: mapper
 * Author:  Evgeniy Lebedev
 * Created: 21-01-2017 22:01
 */
public interface Picture extends Identified {
	byte[] getContent();

	void setContent(byte[] content);

	Image getImage();

	void setImage(Image image);
}
