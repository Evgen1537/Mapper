package com.evgenltd.extractor.entity;

import javafx.scene.image.Image;

import java.awt.image.BufferedImage;

/**
 * <p>Project: mapper</p>
 * <p>Author:  Evgeniy Lebedev</p>
 * <p>Created: 28-01-2017 20:02</p>
 */
public class ResFile extends CacheFile {

	private int resourceVersion;
	private Image image;

	public int getResourceVersion() {
		return resourceVersion;
	}

	public void setResourceVersion(int resourceVersion) {
		this.resourceVersion = resourceVersion;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

}
