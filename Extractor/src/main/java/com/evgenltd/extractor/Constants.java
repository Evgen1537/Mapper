package com.evgenltd.extractor;

import com.evgenltd.extractor.entity.CacheFile;
import javafx.scene.image.Image;

import java.awt.color.ColorSpace;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.io.File;
import java.util.Comparator;

/**
 * <p>Project: mapper</p>
 * <p>Author:  Evgeniy Lebedev</p>
 * <p>Created: 28-01-2017 01:29</p>
 */
public class Constants {

	public static final String RES_ID_URL = "http://game.havenandhearth.com/hres/";
	public static final String CACHE_FOLDER_PATH = System.getenv("APPDATA")
			+ File.separator + "Haven and Hearth"
			+ File.separator + "data";

	public static final Image FOLDER = new Image("/image/folder-horizontal.png");
	public static final Image FILE = new Image("/image/blue-document.png");

	public static final double MAX_SIZE = 400;
	public static final int TILE_SIZE = 100;

	public static final String CACHE_FILE_NAME_PATTERN = ".{16}\\.\\d+";

	public static final String HAFEN_RES = "hafen-res.jar";

	public static final String RESOURCE_PREFIX = "res/";
	public static final String GRAPHIC_RESOURCE_PREFIX = "gfx";

	public static ComponentColorModel COLOR_MODEL = new ComponentColorModel(
			ColorSpace.getInstance(ColorSpace.CS_sRGB),
			new int[]{8, 8, 8, 8},
			true,
			false,
			ComponentColorModel.TRANSLUCENT,
			DataBuffer.TYPE_BYTE
	);

	public static final Comparator<CacheFile> CACHE_FILE_COMPARATOR = Comparator.comparing(CacheFile::getName);

}
