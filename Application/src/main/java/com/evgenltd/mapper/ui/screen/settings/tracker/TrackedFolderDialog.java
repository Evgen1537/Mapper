package com.evgenltd.mapper.ui.screen.settings.tracker;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.bean.TrackerBean;
import com.evgenltd.mapper.core.entity.FolderEntry;
import com.evgenltd.mapper.core.enums.FolderState;
import com.evgenltd.mapper.core.util.Utils;
import com.evgenltd.mapper.ui.screen.DialogScreen;
import com.evgenltd.mapper.ui.util.UIConstants;
import com.evgenltd.mapper.ui.util.UIUtils;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 25-08-2016 21:56
 */
public class TrackedFolderDialog extends DialogScreen<Void> {

	@FXML private Button removeFolder;
	@FXML private Button markAsActual;
	@FXML private Button markAsDeleted;
	@FXML private ListView<FolderEntry> folderEntryListView;

	private final TrackerBean trackerBean = Context.get().getTrackerBean();

	public TrackedFolderDialog() {
		initUI();
		loadData();
	}

	@Override
	protected String getTitle() {
		return "Tracked folders";
	}

	@Override
	protected List<ButtonType> getButtonTypes() {
		return Collections.singletonList(ButtonType.CLOSE);
	}

	@Override
	protected Void resultConverter(ButtonType buttonType) {
		trackerBean.setTrackedFolderChangingListener(null);
		return null;
	}

	private void initUI()	{

		final Stage dialogStage = (Stage)getDialog().getDialogPane().getScene().getWindow();
		dialogStage.getIcons().add(UIConstants.FOLDER_STAMP);
		getDialog().setResizable(true);

		removeFolder.setDisable(true);
		markAsActual.setDisable(true);
		markAsDeleted.setDisable(true);

		folderEntryListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		folderEntryListView.getSelectionModel().getSelectedItems().addListener(this::listSelectionChanged);
		folderEntryListView.setCellFactory(param -> new FolderEntryListCell());

		trackerBean.setTrackedFolderChangingListener(this::trackedFoldersChanged);

	}

	private void loadData()	{
		fillFolderEntryListView(trackerBean.loadAllFolderEntry());
	}

	private void fillFolderEntryListView(@NotNull final List<FolderEntry> folderEntryList)	{
		final List<FolderEntry> prevSelectedItemList = new ArrayList<>(folderEntryListView.getSelectionModel().getSelectedItems());
		folderEntryListView.getItems().setAll(folderEntryList);
		prevSelectedItemList.forEach(folderEntry -> folderEntryListView.getSelectionModel().select(folderEntry));
	}

	private void trackedFoldersChanged(final List<FolderEntry> folderEntryList)	{
		Platform.runLater(() -> fillFolderEntryListView(folderEntryList));
	}

	private void listSelectionChanged(Observable observable) {
		final boolean selectionEmpty = folderEntryListView.getSelectionModel().isEmpty();
		removeFolder.setDisable(selectionEmpty);
		markAsActual.setDisable(selectionEmpty);
		markAsDeleted.setDisable(selectionEmpty);
	}

	@FXML
	private void handleAddFolder(ActionEvent event) {
		UIUtils.askDirectory().ifPresent(selectedDirectory -> {
			trackerBean.addFolderEntry(selectedDirectory.getAbsolutePath());
			loadData();
		});
	}

	@FXML
	private void handleAddAllFolder(ActionEvent event) {
		UIUtils.askDirectory().ifPresent(selectedDirectory -> {

			if(!Utils.checkDirectory(selectedDirectory))	{
				return;
			}

			if(selectedDirectory.list().length == 0)	{
				return;
			}

			for(final String subPath : selectedDirectory.list()) {
				final File subFolder = new File(selectedDirectory, subPath);
				if(Utils.checkDirectory(subFolder))	{
					trackerBean.addFolderEntry(subFolder.getAbsolutePath());
				}
			}

			loadData();

		});
	}

	@FXML
	private void handleRemoveFolder(ActionEvent event) {
		folderEntryListView
				.getSelectionModel()
				.getSelectedItems()
				.forEach(trackerBean::deleteFolderEntry);
		loadData();
	}

	@FXML
	private void handleMarkAsActual(ActionEvent event) {
		folderEntryListView
				.getSelectionModel()
				.getSelectedItems()
				.forEach(trackerBean::markFolderEntryAsActual);
		loadData();
	}

	@FXML
	private void handleMarkAsDeleted(ActionEvent event) {
		folderEntryListView
				.getSelectionModel()
				.getSelectedItems()
				.forEach(trackerBean::markFolderEntryAsDeleted);
		loadData();
	}

	@FXML
	private void handleRefreshFolderList(ActionEvent event) {
		loadData();
	}

	//

	private static class FolderEntryListCell extends ListCell<FolderEntry>	{
		@Override
		protected void updateItem(FolderEntry item, boolean empty) {

			super.updateItem(item, empty);

			if(empty || item == null)	{

				setText("");
				setGraphic(null);

			}else if(Objects.equals(item.getState(), FolderState.DELETED))	{

				setText(item.getSessionPath());
				setTextFill(Color.GRAY);
				setGraphic(new ImageView(UIConstants.CROSS));

			}else {

				setText(item.getSessionPath());
				setTextFill(Color.BLACK);
				setGraphic(null);
			}

		}
	}
}
