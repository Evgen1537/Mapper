package com.evgenltd.mapper.ui.util;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 13-06-2016 01:31
 */
public class UIConstants {

	// size

	public static final Integer MARKER_SIZE = 32;
	public static final Integer MARKER_WITH_OUTLINE_SIZE = 44;
	public static final int LAYER_OUTLINE_WIDTH = 6;
	public static final int SELECTION_CLICK_GAP = 3;	// from SelectionRect
	public static final int RULER_THICKNESS = 30;
	public static final int MARKER_POINT_SIZE = 10;
	public static final int RULER_BORDER_THICKNESS = 1;
	public static final double MARKER_POINT_OUTLINE_WIDTH = 4;
	public static final double SUGGESTED_OUTLINE_WIDTH = 2;
	public static final double SELECTION_OUTLINE_WIDTH = 2;
	public static final double HIGHLIGHT_OUTLINE_WIDTH = 2;
	public static final double AREA_OUTLINE_BORDER_WIDTH = 2;
	public static final double TRACK_WIDTH = 4;
	public static final double AREA_OPACITY = 0.3;

	// color

	public static final Color GRID_COLOR = Color.RED;

	public static final Color SELECTION_COLOR = Color.GREEN;
	public static final Color HIGHLIGHT_COLOR = Color.CADETBLUE;

	public static final Color POINT_COLOR = Color.YELLOW;
	public static final Color SUGGESTED_POINT_COLOR = Color.GREEN;
	public static final Color SUGGESTED_OUTLINE_COLOR = Color.BLACK;
	public static final Color DEFAULT_MARKER_COLOR = Color.YELLOW;

	public static final Color ESSENCE_COLOR = Color.valueOf("#9D45CB");
	public static final Color SUBSTANCE_COLOR = Color.valueOf("#D0BC28");
	public static final Color VITALITY_COLOR = Color.valueOf("#6B9025");

	public static final Color STATUS_CORRECT = Color.valueOf("#21B232");
	public static final Color STATUS_INCORRECT = Color.valueOf("#B11111");

	// images

	public static final Image MAP_ICON = new Image("/image/map.png");
	public static final Image CAVE_ICON = new Image("/image/cave.png");
	public static final Image HOME_ICON = new Image("/image/home.png");
	public static final Image SMILEY = new Image("/image/smiley.png");

	public static final Image EXCLAMATION = new Image("/image/exclamation.png");
	public static final Image EXCLAMATION_RED = new Image("/image/exclamation-red.png");
	public static final Image INFORMATION_WHITE = new Image("/image/information-white.png");
	public static final Image CLIPBOARD_TASK = new Image("/image/clipboard-task.png");
	public static final Image EYE = new Image("/image/eye.png");
	public static final Image EYE_HALF = new Image("/image/eye-half.png");
	public static final Image EYE_CLOSE = new Image("/image/eye-close.png");
	public static final Image LAYERS_ARRANGE = new Image("/image/layers-arrange.png");
	public static final Image GEAR = new Image("/image/gear.png");
	public static final Image CROSS = new Image("/image/cross.png");
	public static final Image FOLDER_STAMP = new Image("/image/folder-stamp.png");
	public static final Image LAYER = new Image("/image/layers32.png");
	public static final Image ESSENCE = new Image("/image/essence.png");
	public static final Image SUBSTANCE = new Image("/image/substance.png");
	public static final Image VITALITY = new Image("/image/vitality.png");
	public static final Image TARGET = new Image("/image/target.png");
	public static final Image TICK = new Image("/image/tick.png");

	// command ids

	public static final String ADD_LAYER_FROM_FILE_SYSTEM = "ADD_LAYER_FROM_FILE_SYSTEM";
	public static final String ADD_MANY_LAYERS_FROM_FILE_SYSTEM = "ADD_MANY_LAYERS_FROM_FILE_SYSTEM";
	public static final String GENERATE_LEVELS = "GENERATE_LEVELS";
	public static final String REFRESH_LAYER = "REFRESH_LAYER";
	public static final String FIND_MATCHES = "FIND_MATCHES";
	public static final String EXPORT_LAYER_TO_FOLDER = "EXPORT_LAYER_TO_FOLDER";

	public static final String MERGE_WITH_GLOBAL = "MERGE_WITH_GLOBAL";
	public static final String MERGE_TOGETHER = "MERGE_TOGETHER";

	public static final String BRING_LAYERS_TO_FRONT = "BRING_LAYERS_TO_FRONT";
	public static final String SEND_LAYERS_TO_BACK = "SEND_LAYERS_TO_BACK";
	public static final String BRING_LAYERS_FORWARD = "BRING_LAYERS_FORWARD";
	public static final String SEND_LAYERS_BACKWARD = "SEND_LAYERS_BACKWARD";

	public static final String GLOBAL_LAYER_GENERATE_LEVELS = "GLOBAL_LAYER_GENERATE_LEVELS";
	public static final String GLOBAL_LAYER_UP = "GLOBAL_LAYER_UP";
	public static final String GLOBAL_LAYER_DOWN = "GLOBAL_LAYER_DOWN";
	public static final String GLOBAL_LAYER_FULL = "GLOBAL_LAYER_FULL";
	public static final String GLOBAL_LAYER_PARTLY = "GLOBAL_LAYER_PARTLY";
	public static final String GLOBAL_LAYER_NONE = "GLOBAL_LAYER_NONE";
	public static final String GLOBAL_EXPORT_LAYER_TO_FOLDER = "GLOBAL_EXPORT_LAYER_TO_FOLDER";

	public static final String MARKER_ADD_POINT = "MARKER_ADD_POINT";
	public static final String MARKER_ADD_AREA = "MARKER_ADD_AREA";
	public static final String MARKER_ADD_TRACK = "MARKER_ADD_TRACK";
	public static final String MARKER_ADD_ENTRANCE = "MARKER_ADD_ENTRANCE";

	public static final String MARKER_HIDE = "MARKER_HIDE";

	public static final String MARKER_EDIT_BEGIN = "MARKER_EDIT_BEGIN";
	public static final String MARKER_EDIT_CANCEL = "MARKER_EDIT_CANCEL";
	public static final String MARKER_EDIT_APPLY = "MARKER_EDIT_APPLY";
	public static final String MARKER_EDIT_ADD_POINT = "MARKER_EDIT_ADD_POINT";
	public static final String MARKER_EDIT_REMOVE_POINT = "MARKER_EDIT_REMOVE_POINT";

	public static final String REMOVE = "REMOVE";

	public static final String VISIBILITY_FULL = "FULL";
	public static final String VISIBILITY_PARTLY = "PARTLY";
	public static final String VISIBILITY_NONE = "NONE";

	public static final String SHOW_RULER = "SHOW_RULER";
	public static final String SHOW_GRID = "SHOW_GRID";

	public static final String START_TRACKING = "START_TRACKING";
	public static final String PAUSE_TRACKING = "PAUSE_TRACKING";
	public static final String TO_PLAYER_POSITION = "TO_PLAYER_POSITION";

	public static final String SELECTION_ALL = "SELECTION_ALL";
	public static final String SELECTION_LAYER = "SELECTION_LAYER";
	public static final String SELECTION_MARKER = "SELECTION_MARKER";

	public static final String UNDO = "UNDO";
	public static final String REDO = "REDO";

	public static final String OPEN_SETTINGS_COMMAND = "OPEN_SETTINGS_COMMAND";
	public static final String IMPORT = "IMPORT";
	public static final String ABOUT = "ABOUT";
	public static final String GLOBAL_MAP_INTEGRATION = "GLOBAL_MAP_INTEGRATION";
}