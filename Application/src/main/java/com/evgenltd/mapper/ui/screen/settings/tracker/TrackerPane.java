package com.evgenltd.mapper.ui.screen.settings.tracker;

import com.evgenltd.mapper.core.entity.Settings;
import com.evgenltd.mapper.core.util.Constants;
import com.evgenltd.mapper.ui.screen.AbstractScreen;
import com.evgenltd.mapper.ui.util.UIUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 21-08-2016 00:15
 */
public class TrackerPane extends AbstractScreen {

	@FXML private TextField mapFolderPath;
	@FXML private CheckBox refreshLayers;
	@FXML private CheckBox addNewLayers;
	@FXML private CheckBox removeSessionFoldersFromDisk;
	@FXML private CheckBox removeSmallSessionFolders;
	@FXML private TextField smallSessionFolderSize;
	@FXML private CheckBox trackPlayerPosition;
	@FXML private TextField positionFilePath;
	@FXML private Button browsePositionFile;

	private File selectedMapFolder;
	private File selectedPositionFilePath;

	public void initUI() {
		smallSessionFolderSize.setDisable(true);
		removeSmallSessionFolders.selectedProperty().addListener(observable -> updateRemoveSmallSessionFoldersControlState());
		smallSessionFolderSize.textProperty().addListener(observable -> updateSmallSessionFolderSizeContent());
		positionFilePath.setDisable(true);
		browsePositionFile.setDisable(true);
		trackPlayerPosition.selectedProperty().addListener(observable -> updatePositionFileControlState());
	}

	public void fillUI(@NotNull final Settings settings) {
		selectedMapFolder = settings.getMapFolder();
		mapFolderPath.setText(selectedMapFolder != null ? selectedMapFolder.getAbsolutePath() : "");
		refreshLayers.setSelected(settings.isRefreshLayers());
		addNewLayers.setSelected(settings.isAddNewLayers());
		removeSessionFoldersFromDisk.setSelected(settings.isRemoveSessionFoldersFromDisk());
		removeSmallSessionFolders.setSelected(settings.isRemoveSmallSessionFolders());
		smallSessionFolderSize.setText(String.valueOf(settings.getSmallSessionFolderSize()));
		trackPlayerPosition.setSelected(settings.isTrackPlayerPosition());
		selectedPositionFilePath = settings.getPlayerPositionFile();
		positionFilePath.setText(selectedPositionFilePath != null ? selectedPositionFilePath.getAbsolutePath() : "");
	}


	public void fillEntity(@NotNull final Settings settings) {
		settings.setMapFolder(selectedMapFolder);
		settings.setRefreshLayers(refreshLayers.isSelected());
		settings.setAddNewLayers(addNewLayers.isSelected());
		settings.setRemoveSessionFoldersFromDisk(removeSessionFoldersFromDisk.isSelected());
		settings.setRemoveSmallSessionFolders(removeSmallSessionFolders.isSelected());

		final String smallSessionFolderSizeValue = smallSessionFolderSize.getText().trim();
		settings.setSmallSessionFolderSize(smallSessionFolderSizeValue.isEmpty()
												   ? Constants.TRACKER_DEFAULT_SMALL_SESSION_FOLDER_SIZE
												   : Long.parseLong(smallSessionFolderSizeValue)
		);

		settings.setTrackPlayerPosition(trackPlayerPosition.isSelected());
		settings.setPlayerPositionFile(selectedPositionFilePath);
	}

	private void updateRemoveSmallSessionFoldersControlState()	{
		smallSessionFolderSize.setDisable(!removeSmallSessionFolders.isSelected());
	}

	private void updatePositionFileControlState()	{
		positionFilePath.setDisable(!trackPlayerPosition.isSelected());
		browsePositionFile.setDisable(!trackPlayerPosition.isSelected());
	}

	private void updateSmallSessionFolderSizeContent()	{

		String textValue = smallSessionFolderSize.getText().trim();

		if(!textValue.matches("\\d*"))	{
			textValue = textValue.replaceAll("[^\\d]","");
			smallSessionFolderSize.setText(textValue);
			return;
		}

		if(textValue.isEmpty())	{
			return;
		}

		final Long longValue = Long.parseLong(textValue);
		if(longValue < 1)	{
			smallSessionFolderSize.setText(String.valueOf(Constants.TRACKER_DEFAULT_SMALL_SESSION_FOLDER_SIZE));
		}

	}
	
	@FXML
	@SuppressWarnings("unused")
	private void handleBrowseMapFolder(ActionEvent event) {
		UIUtils.askDirectory().ifPresent(file -> {
			selectedMapFolder = file;
			mapFolderPath.setText(file.getAbsolutePath());
		});
	}

	@FXML
	@SuppressWarnings("unused")
	private void handleBrowsePositionFile(ActionEvent event)	{
		UIUtils.askFile().ifPresent(file -> {
			selectedPositionFilePath = file;
			positionFilePath.setText(file.getAbsolutePath());
		});
	}

	@FXML
	@SuppressWarnings("unused")
	private void handleViewTrackedFolder(ActionEvent event) {
		final TrackedFolderDialog trackedFolderDialog = new TrackedFolderDialog();
		trackedFolderDialog.showAndWait();
	}
}
