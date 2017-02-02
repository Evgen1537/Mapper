package com.evgenltd.mapper.ui.screen.main.toolbar;

import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.component.command.CommandManager;
import com.evgenltd.mapper.ui.screen.AbstractScreen;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.controlsfx.control.action.ActionUtils;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 27-06-2016 21:01
 */
public class HomeTab extends AbstractScreen {

	@FXML private MenuButton addLayerGroup;
	@FXML private MenuItem addSession;
	@FXML private MenuItem addManySessions;
	@FXML private Button mergeTogether;

	@FXML private Button generateLevels;
	@FXML private Button refreshLayer;
	@FXML private Button findMatches;
	@FXML private Button deleteLayer;
	@FXML private Button exportToFolder;

	@FXML private MenuButton orderGroup;
	@FXML private MenuItem bringToFront;
	@FXML private MenuItem bringForward;
	@FXML private MenuItem sendBackward;
	@FXML private MenuItem sendToBack;

	@FXML private MenuButton visibleGroup;
	@FXML private MenuItem fullVisible;
	@FXML private MenuItem partlyVisible;
	@FXML private MenuItem invisible;

	@FXML private MenuButton addMarkerGroup;
	@FXML private MenuItem addPointMarker;
	@FXML private MenuItem addAreaMarker;
	@FXML private MenuItem addTrackMarker;
	@FXML private MenuItem addEntranceMarker;

	@FXML private Button editMarker;
	@FXML private Button deleteMarker;
	@FXML private ToggleButton hideMarkers;

	@FXML private ToggleGroup tracker;
	@FXML private ToggleButton startTracking;
	@FXML private ToggleButton pauseTracking;
	@FXML private Button toPlayer;

	@FXML private MenuButton selectionGroup;
	@FXML private ToggleGroup selection;
	@FXML private RadioMenuItem selectionAll;
	@FXML private RadioMenuItem selectionLayer;
	@FXML private RadioMenuItem selectionMarker;
	@FXML private ToggleButton showRuler;
	@FXML private ToggleButton showGrid;

	private final CommandManager commandManager = UIContext.get().getCommandManager();

	public HomeTab() {
		initUI();
	}

	private void initUI()	{
		commandManager.configureMenuItem(UIConstants.ADD_LAYER_FROM_FILE_SYSTEM, addSession);
		commandManager.configureMenuItem(UIConstants.ADD_MANY_LAYERS_FROM_FILE_SYSTEM, addManySessions);

		commandManager.configureButton(UIConstants.MERGE_TOGETHER, mergeTogether);

		commandManager.configureButton(UIConstants.GENERATE_LEVELS, generateLevels);
		commandManager.configureButton(UIConstants.REFRESH_LAYER, refreshLayer);
		commandManager.configureButton(UIConstants.FIND_MATCHES, findMatches);
		commandManager.configureButton(UIConstants.REMOVE, deleteLayer);
		commandManager.configureButton(UIConstants.EXPORT_LAYER_TO_FOLDER, exportToFolder);

		commandManager.configureMenuItem(UIConstants.BRING_LAYERS_TO_FRONT, bringToFront);
		commandManager.configureMenuItem(UIConstants.BRING_LAYERS_FORWARD, bringForward);
		commandManager.configureMenuItem(UIConstants.SEND_LAYERS_BACKWARD, sendBackward);
		commandManager.configureMenuItem(UIConstants.SEND_LAYERS_TO_BACK, sendToBack);

		commandManager.configureMenuItem(UIConstants.VISIBILITY_FULL, fullVisible);
		commandManager.configureMenuItem(UIConstants.VISIBILITY_PARTLY, partlyVisible);
		commandManager.configureMenuItem(UIConstants.VISIBILITY_NONE, invisible);

		commandManager.configureMenuItem(UIConstants.MARKER_ADD_POINT, addPointMarker);
		commandManager.configureMenuItem(UIConstants.MARKER_ADD_AREA, addAreaMarker);
		commandManager.configureMenuItem(UIConstants.MARKER_ADD_TRACK, addTrackMarker);
		commandManager.configureMenuItem(UIConstants.MARKER_ADD_ENTRANCE, addEntranceMarker);

		commandManager.configureButton(UIConstants.MARKER_EDIT_BEGIN, editMarker);
		commandManager.configureButton(UIConstants.REMOVE, deleteMarker);
		commandManager.configureButton(UIConstants.MARKER_HIDE, hideMarkers);

		commandManager.configureButton(UIConstants.START_TRACKING, startTracking, ActionUtils.ActionTextBehavior.HIDE);
		commandManager.configureButton(UIConstants.PAUSE_TRACKING, pauseTracking, ActionUtils.ActionTextBehavior.HIDE);
		commandManager.configureButton(UIConstants.TO_PLAYER_POSITION, toPlayer);

		commandManager.configureMenuItem(UIConstants.SELECTION_ALL, selectionAll);
		commandManager.configureMenuItem(UIConstants.SELECTION_LAYER, selectionLayer);
		commandManager.configureMenuItem(UIConstants.SELECTION_MARKER, selectionMarker);

		commandManager.configureButton(UIConstants.SHOW_RULER, showRuler);
		commandManager.configureButton(UIConstants.SHOW_GRID, showGrid);

	}
}
