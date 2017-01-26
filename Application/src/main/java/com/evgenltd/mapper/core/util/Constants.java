package com.evgenltd.mapper.core.util;

import com.evgenltd.mapper.core.entity.Layer;
import com.evgenltd.mapper.core.entity.impl.EntityFactory;
import math.geom2d.Point2D;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 13-06-2016 01:31
 */
public class Constants {

	public static final String DEBUG_MODE_FLAG = "-debug";
	public static final String SKIP_BACKUP_ON_STARTUP = "-skip-backup";
	public static final String SETTINGS_FILE = "config.xml";

	public static final Integer TILE_SIZE = 100;
	public static final String TILE_NAME_PATTERN = "tile_.*_.*\\.png";
	public static final double MIN_LEVEL = 1D;
	public static final double MAX_LEVEL = 5D;

	public static final String IDS_DESCRIPTOR = "ids.txt";

	public static final long TRACKER_REMINDER_DELAY = 2000;
	public static final long TRACKER_DEFAULT_SMALL_SESSION_FOLDER_SIZE = 10;
	public static final String TRACKER_CURRENT_SESSION_FILE = "currentsession.js";

	public static final long MARKER_ORDER_POSITION = 1000;
	public static final long NEW_NODE_OIRDER_POSITION = 2000;

	public static final String MARKER_ICON_NAME_NONE = "None";
	public static final String MARKER_ICON_NAME_CAVE = "Cave";
	public static final String MARKER_ICON_NAME_CLAIM = "Claim";
	public static final String MARKER_ICON_NAME_CLAY = "Clay";
	public static final String MARKER_ICON_NAME_SOIL = "Soil";
	public static final String MARKER_ICON_NAME_VILLAGE = "Village";
	public static final String MARKER_ICON_NAME_WATER = "Water";

	public static final Point2D ZERO_POINT = new Point2D(0,0);

	public static final Layer NONE = EntityFactory.createLayer();
	static {
		NONE.setName("None");
	}
}
