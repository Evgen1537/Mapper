package com.evgenltd.extractor;

import com.evgenltd.extractor.entity.CacheFile;
import javafx.scene.image.Image;

import java.awt.color.ColorSpace;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.util.Comparator;

/**
 * <p>Project: mapper</p>
 * <p>Author:  Evgeniy Lebedev</p>
 * <p>Created: 28-01-2017 01:29</p>
 */
public class Constants {

	public static final String RES_ID_URL = "http://game.havenandhearth.com/hres/";

	public static final Image FOLDER = new Image("/image/folder-horizontal.png");
	public static final Image FILE = new Image("/image/blue-document.png");

	public static final double MAX_SIZE = 400;
	public static final int TILE_SIZE = 100;

	public static final String CACHE_FILE_NAME_PATTERN = ".{16}\\.\\d+";

	public static final String HAFEN_RES = "hafen-res.jar";
	public static final String RES_ROOT = "res";

	public static ComponentColorModel COLOR_MODEL = new ComponentColorModel(
			ColorSpace.getInstance(ColorSpace.CS_sRGB),
			new int[]{8, 8, 8, 8},
			true,
			false,
			ComponentColorModel.TRANSLUCENT,
			DataBuffer.TYPE_BYTE
	);

	public static final Comparator<CacheFile> CACHE_FILE_COMPARATOR = new Comparator<CacheFile>() {
		@Override
		public int compare(CacheFile o1, CacheFile o2) {
			return o1.getName().compareTo(o2.getName());
		}
	};

}
