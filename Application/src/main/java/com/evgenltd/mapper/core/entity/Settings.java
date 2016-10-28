package com.evgenltd.mapper.core.entity;

import com.evgenltd.mapper.core.enums.SelectionMode;
import com.evgenltd.mapper.core.util.Constants;
import javafx.scene.input.KeyCombination;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 18-06-2016 19:50
 */
public class Settings {

	// general
	private boolean overwriteTiles;
	private double partlyVisibilityAlpha = 0.5;
	private boolean showMarkerQuality = true;

	// tracker
	private boolean enableTracker;
	private File mapFolder;
	private boolean refreshLayers;
	private boolean addNewLayers;
	private boolean removeSessionFoldersFromDisk;
	private boolean removeSmallSessionFolders;
	private long smallSessionFolderSize = Constants.TRACKER_DEFAULT_SMALL_SESSION_FOLDER_SIZE;
	private boolean trackPlayerPosition;
	private File playerPositionFile;

	// toolbar settings
	private SelectionMode selectionMode = SelectionMode.ALL;
	private boolean hideMarkers;
	private boolean hideRuler;
	private boolean hideGrid = true;

	// system
	private File lastUsedFolder;
	private double viewportCentroidX;
	private double viewportCentroidY;

	private Map<String,KeyCombination> hotKeyMap = new HashMap<>();

	public boolean isOverwriteTiles() {
		return overwriteTiles;
	}
	public void setOverwriteTiles(final boolean overwriteTiles) {
		this.overwriteTiles = overwriteTiles;
	}

	public double getPartlyVisibilityAlpha() {
		return partlyVisibilityAlpha;
	}
	public void setPartlyVisibilityAlpha(final double partlyVisibilityAlpha) {
		this.partlyVisibilityAlpha = partlyVisibilityAlpha;
	}

	public boolean isShowMarkerQuality() {
		return showMarkerQuality;
	}
	public void setShowMarkerQuality(boolean showMarkerQuality) {
		this.showMarkerQuality = showMarkerQuality;
	}

	// tracker

	public File getMapFolder() {
		return mapFolder;
	}
	public void setMapFolder(File mapFolder) {
		this.mapFolder = mapFolder;
	}

	public boolean isEnableTracker() {
		return enableTracker;
	}
	public void setEnableTracker(boolean enableTracker) {
		this.enableTracker = enableTracker;
	}

	public boolean isRefreshLayers() {
		return refreshLayers;
	}
	public void setRefreshLayers(boolean refreshLayers) {
		this.refreshLayers = refreshLayers;
	}

	public boolean isAddNewLayers() {
		return addNewLayers;
	}
	public void setAddNewLayers(boolean addNewLayers) {
		this.addNewLayers = addNewLayers;
	}

	public boolean isRemoveSessionFoldersFromDisk() {
		return removeSessionFoldersFromDisk;
	}
	public void setRemoveSessionFoldersFromDisk(boolean removeSessionFoldersFromDisk) {
		this.removeSessionFoldersFromDisk = removeSessionFoldersFromDisk;
	}

	public boolean isRemoveSmallSessionFolders() {
		return removeSmallSessionFolders;
	}
	public void setRemoveSmallSessionFolders(boolean removeSmallSessionFolders) {
		this.removeSmallSessionFolders = removeSmallSessionFolders;
	}

	public long getSmallSessionFolderSize() {
		return smallSessionFolderSize;
	}
	public void setSmallSessionFolderSize(long smallSessionFolderSize) {
		this.smallSessionFolderSize = smallSessionFolderSize;
	}

	public File getPlayerPositionFile() {
		return playerPositionFile;
	}
	public void setPlayerPositionFile(File playerPositionFile) {
		this.playerPositionFile = playerPositionFile;
	}

	public boolean isTrackPlayerPosition() {
		return trackPlayerPosition;
	}
	public void setTrackPlayerPosition(boolean trackPlayerPosition) {
		this.trackPlayerPosition = trackPlayerPosition;
	}

	// toolbar settings

	public boolean isHideMarkers() {
		return hideMarkers;
	}
	public void setHideMarkers(final boolean hideMarkers) {
		this.hideMarkers = hideMarkers;
	}

	public SelectionMode getSelectionMode() {
		return selectionMode;
	}
	public void setSelectionMode(SelectionMode selectionMode) {
		this.selectionMode = selectionMode;
	}

	public boolean isHideRuler() {
		return hideRuler;
	}
	public void setHideRuler(boolean hideRuler) {
		this.hideRuler = hideRuler;
	}

	public boolean isHideGrid() {
		return hideGrid;
	}
	public void setHideGrid(boolean hideGrid) {
		this.hideGrid = hideGrid;
	}

	// system

	public File getLastUsedFolder() {
		return lastUsedFolder;
	}
	public void setLastUsedFolder(File lastUsedFolder) {
		this.lastUsedFolder = lastUsedFolder;
	}

	public double getViewportCentroidX() {
		return viewportCentroidX;
	}
	public void setViewportCentroidX(double viewportCentroidX) {
		this.viewportCentroidX = viewportCentroidX;
	}

	public double getViewportCentroidY() {
		return viewportCentroidY;
	}
	public void setViewportCentroidY(double viewportCentroidY) {
		this.viewportCentroidY = viewportCentroidY;
	}

	// hotkeys

	public KeyCombination getHotKey(@NotNull final String commandId) {
		return hotKeyMap.get(commandId);
	}

	public void putHotKey(
			@NotNull String key,
			@NotNull KeyCombination keyCombination
	) {
		hotKeyMap.putIfAbsent(key, keyCombination);
	}

	public Map<String, KeyCombination> getHotKeyMap() {
		return Collections.unmodifiableMap(hotKeyMap);
	}

	public void setHotKeyMap(@NotNull final Map<String, KeyCombination> hotKeyMap) {
		this.hotKeyMap = hotKeyMap;
	}

	// copy

	public Settings copy()	{
		final Settings copy = new Settings();
		copy.overwriteTiles = isOverwriteTiles();
		copy.hideMarkers = isHideMarkers();
		copy.selectionMode = getSelectionMode();
		copy.partlyVisibilityAlpha = getPartlyVisibilityAlpha();
		copy.hideRuler = isHideRuler();
		copy.hideGrid = isHideGrid();
		copy.enableTracker = enableTracker;
		copy.mapFolder = mapFolder;
		copy.refreshLayers = refreshLayers;
		copy.addNewLayers = addNewLayers;
		copy.removeSessionFoldersFromDisk = removeSessionFoldersFromDisk;
		copy.removeSmallSessionFolders = removeSmallSessionFolders;
		copy.smallSessionFolderSize = smallSessionFolderSize;
		copy.trackPlayerPosition = trackPlayerPosition;
		copy.playerPositionFile = playerPositionFile;
		copy.setHotKeyMap(getHotKeyMap());
		return copy;
	}
}
