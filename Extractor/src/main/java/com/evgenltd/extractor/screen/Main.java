package com.evgenltd.extractor.screen;

import com.evgenltd.extractor.Constants;
import com.evgenltd.extractor.cellfactory.CacheFileListCell;
import com.evgenltd.extractor.cellfactory.ResourceEntryTreeCell;
import com.evgenltd.extractor.entity.CacheFile;
import com.evgenltd.extractor.entity.CacheFileBuilder;
import com.evgenltd.extractor.entity.MapGridFile;
import com.evgenltd.extractor.entity.ResFile;
import com.evgenltd.extractor.util.ImageCache;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.MaskerPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
