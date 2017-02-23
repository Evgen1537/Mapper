package com.evgenltd.extractor.screen;

import com.evgenltd.extractor.Constants;
import com.evgenltd.extractor.cellfactory.CacheFileListCell;
import com.evgenltd.extractor.entity.CacheFile;
import com.evgenltd.extractor.entity.CacheFileBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import org.controlsfx.control.MaskerPane;

import java.io.IOException;

/**
 * <p>Project: mapper</p>
 * <p>Author:  Evgeniy Lebedev</p>
 * <p>Created: 28-01-2017 00:12</p>
 */
public class Main {

	@FXML
	private BorderPane root;
	@FXML
	private TextField quickSearch;
	@FXML
	private ListView<CacheFile> dataList;
	@FXML
	private MaskerPane dataListLoadIndicator;
	@FXML
	private ImageView cacheImageViewer;
	@FXML
	private TextArea cacheFileDescription;

	private GameCacheModel gameCacheModel;

	public Main() {

	}

	// lifecycle

	public void init() {

		try {
			final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
			loader.setControllerFactory(clazz -> Main.this);
			loader.load();
		}catch (IOException e) {
			throw new RuntimeException(e);
		}

		gameCacheModel = new GameCacheModel();
		gameCacheModel.init();

		gameCacheModel.quickSearchProperty().bind(quickSearch.textProperty());

		dataList.setCellFactory(param -> new CacheFileListCell());
		dataList.getSelectionModel().selectedItemProperty().addListener(observable -> handleDataListItemSelection());
		dataList.setItems(gameCacheModel.getFilteredDataList());

		refresh();
	}

	public void refresh() {
		loadDataList();
	}

	public void destroy() {
	}

	// properties

	public BorderPane getRoot() {
		return root;
	}


	// handlers

	private void handleDataListItemSelection() {

		final CacheFile cacheFile = dataList.getSelectionModel().getSelectedItem();
		if (cacheFile.getName().startsWith(Constants.RESOURCE_PREFIX + Constants.GRAPHIC_RESOURCE_PREFIX)) {
			final Image image = gameCacheModel.loadImageResource(cacheFile);
			cacheImageViewer.setImage(image);
			return;
		}

		final CacheFile detailedCacheFile = CacheFileBuilder.buildWithDetails(cacheFile);
		cacheFileDescription.setText(detailedCacheFile.toString());

	}

	@FXML
	private void handleApplyFilter(ActionEvent actionEvent) {
		loadDataList();
	}

	private void loadDataList() {
		dataListLoadIndicator.setVisible(true);
		gameCacheModel.loadCachedFiles(() -> dataListLoadIndicator.setVisible(false));
	}

}
