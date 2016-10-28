package com.evgenltd.mapper.core.bean;

import com.evgenltd.mapper.core.entity.Settings;
import com.evgenltd.mapper.core.enums.SelectionMode;
import com.evgenltd.mapper.core.util.Constants;
import javafx.scene.input.KeyCombination;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 18-06-2016 20:06
 */
@Component
public class SettingsBean {

	private static final Logger log = LogManager.getLogger(SettingsBean.class);

	@Autowired
	private XStreamMarshaller xStreamMarshaller;

	private Settings settings;

	@PostConstruct
	public void postConstruct() {
		try(FileInputStream loadStream = new FileInputStream(Constants.SETTINGS_FILE)) {
			settings = (Settings)xStreamMarshaller.unmarshalInputStream(loadStream);
		}catch(Exception e)   {
			log.info("Failed to load settings");
			settings = new Settings();
		}
	}

	@PreDestroy
	public void preDestroy() {
		try(FileOutputStream saveStream = new FileOutputStream(Constants.SETTINGS_FILE)) {
			xStreamMarshaller.marshalOutputStream(settings, saveStream);
		}catch(Exception e)   {
			log.error("Failed to save settings", e);
		}
	}

	public void save()	{
		preDestroy();
	}

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	// ############################################################
	// #                                                          #
	// #                        Options                           #
	// #                                                          #
	// ############################################################

	// general

	public boolean isOverwriteTiles() {
		return settings.isOverwriteTiles();
	}

	public double getPartlyVisibilityAlpha() {
		return settings.getPartlyVisibilityAlpha();
	}

	public boolean isShowMarkerQuality() {
		return settings.isShowMarkerQuality();
	}

	// tracker

	public boolean isEnableTracker() {
		return settings.isEnableTracker();
	}
	public void setEnableTracker(boolean enableTracker) {
		settings.setEnableTracker(enableTracker);
	}

	public File getMapFolder() {
		return settings.getMapFolder();
	}

	public boolean isRefreshLayers() {
		return settings.isRefreshLayers();
	}

	public boolean isAddNewLayers() {
		return settings.isAddNewLayers();
	}

	public boolean isRemoveSessionFoldersFromDisk() {
		return settings.isRemoveSessionFoldersFromDisk();
	}

	public boolean isRemoveSmallSessionFolders()	{
		return settings.isRemoveSmallSessionFolders();
	}

	public long getSmallSessionFolderSize()	{
		return settings.getSmallSessionFolderSize();
	}

	public boolean isTrackPlayerPosition() {
		return settings.isTrackPlayerPosition();
	}

	public File getPlayerPositionFile() {
		return settings.getPlayerPositionFile();
	}

	// toolbar settings

	public boolean isHideMarkers() {
		return settings.isHideMarkers();
	}
	public void setHideMarkers(final boolean hideMarkers) {
		settings.setHideMarkers(hideMarkers);
	}

	public SelectionMode getSelectionMode() {
		return settings.getSelectionMode();
	}
	public void setSelectionMode(SelectionMode selectionMode) {
		settings.setSelectionMode(selectionMode);
	}

	public boolean isHideRuler() {
		return settings.isHideRuler();
	}
	public void setHideRuler(boolean hideRuler) {
		settings.setHideRuler(hideRuler);
	}

	public boolean isHideGrid() {
		return settings.isHideGrid();
	}
	public void setHideGrid(boolean hideGrid) {
		settings.setHideGrid(hideGrid);
	}

	// system

	public File getLastUsedFolder() {
		return settings.getLastUsedFolder();
	}
	public void setLastUsedFolder(File lastUsedFolder) {
		settings.setLastUsedFolder(lastUsedFolder);
	}

	public double getViewportCentroidX() {
		return settings.getViewportCentroidX();
	}
	public void setViewportCentroidX(double viewportCentroidX) {
		settings.setViewportCentroidX(viewportCentroidX);
	}

	public double getViewportCentroidY() {
		return settings.getViewportCentroidY();
	}
	public void setViewportCentroidY(double viewportCentroidY) {
		settings.setViewportCentroidY(viewportCentroidY);
	}

	// hotkey

	public KeyCombination getHotKey(@NotNull String commandId) {
		return settings.getHotKey(commandId);
	}

	public void putHotKey(
			@NotNull String key,
			@NotNull KeyCombination keyCombination
	) {
		settings.putHotKey(key, keyCombination);
	}
}
