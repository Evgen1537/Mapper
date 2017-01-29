package com.evgenltd.extractor.util;

import haven.Resource;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.rmi.Remote;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Project: mapper</p>
 * <p>Author:  Evgeniy Lebedev</p>
 * <p>Created: 28-01-2017 06:22</p>
 */
public class ImageCache {

	private static final ImageCache instance = new ImageCache();

	private final Map<String,BufferedImage> awtImageCache = new HashMap<>();
	private final Map<Long,Image> tileImageCache = new HashMap<>();

	public static ImageCache get() {
		return instance;
	}

	public BufferedImage getResource(@NotNull final String path) {
		Resource.Image hafenImage = getRemoteResource(path);
		if (hafenImage == null) {
			hafenImage = getLocalResource(path);
		}
		if (hafenImage == null) {
			return null;
		}
		final BufferedImage awtImage = hafenImage.img;
		awtImageCache.put(path, awtImage);
		return awtImage;
	}

	private Resource.Image getRemoteResource(@NotNull final String path) {
		try {
			return Resource.remote().loadwait(path).layer(Resource.imgc);
		}catch (Exception e) {
			return null;
		}
	}

	private Resource.Image getLocalResource(@NotNull final String path) {
		try {
			return Resource.local().loadwait(path).layer(Resource.imgc);
		}catch (Exception e) {
			return null;
		}
	}

}
