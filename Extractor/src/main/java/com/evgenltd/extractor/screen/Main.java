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
	private StackPane root;
	@FXML
	private VBox menuPane;
	@FXML
	private BorderPane gameCachePane;
	@FXML
	private BorderPane resourcesPane;
	@FXML
	private TreeView<ResourceEntry> resourceTree;
	@FXML
	private ImageView resourceViewer;
	@FXML
	private ProgressIndicator resourceLoadingIndicator;
	@FXML
	private Label noImageLabel;
	@FXML
	private ListView<CacheFile> dataList;
	@FXML
	private ProgressIndicator dataListLoadIndicator;
	@FXML
	private ImageView cacheImageViewer;
	@FXML
	private TextArea cacheFileDescription;

	private ResourceViewModel resourceViewModel;
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

		resourceViewModel = new ResourceViewModel();
		resourceViewModel.init();
		gameCacheModel = new GameCacheModel();
		gameCacheModel.init();

		resourceTree.setShowRoot(false);
		resourceTree.setCellFactory(param -> new ResourceEntryTreeCell());
		resourceTree.getSelectionModel().selectedIndexProperty().addListener(observable -> handleResourceTreeItemSelection());

		dataList.setCellFactory(param -> new CacheFileListCell());
		dataList.getSelectionModel().selectedItemProperty().addListener(observable -> handleDataListItemSelection());

	}

	public void refresh() {

	}

	public void destroy() {
		resourceViewModel.destroy();
	}

	// properties

	public StackPane getRoot() {
		return root;
	}


	// handlers

	@FXML
	private void handleViewGameCache(ActionEvent actionEvent) {
		menuPane.setVisible(false);
		gameCachePane.setVisible(true);
		resourcesPane.setVisible(false);
		dataListLoadIndicator.setVisible(true);

		gameCacheModel.loadCachedFiles(result -> {
			dataList.getItems().setAll(result);
			dataListLoadIndicator.setVisible(false);
		});
	}

	@FXML
	private void handleViewGameResources(ActionEvent actionEvent) {
		resourceTree.setRoot(resourceViewModel.loadResourceDescriptors());
		menuPane.setVisible(false);
		gameCachePane.setVisible(false);
		resourcesPane.setVisible(true);
	}

	@FXML
	private void handleBackToHome(ActionEvent actionEvent) {
		menuPane.setVisible(true);
		gameCachePane.setVisible(false);
		resourcesPane.setVisible(false);
	}

	private void handleResourceTreeItemSelection() {
		final TreeItem<ResourceEntry> item = resourceTree.getSelectionModel().getSelectedItem();
		if (item == null) {
			return;
		}

		final ResourceEntry resourceEntry = item.getValue();
		if (resourceEntry.isDirectory()) {
			return;
		}

		handleResourceLoadingStarted();

		resourceViewModel.loadImageResource(resourceEntry, this::handleResourceLoadingFinished);
	}

	private void handleResourceLoadingStarted() {
		noImageLabel.setVisible(false);
		resourceLoadingIndicator.setVisible(true);
		resourceViewer.setVisible(false);
	}

	private void handleResourceLoadingFinished(@Nullable final Image image) {
		resourceLoadingIndicator.setVisible(false);
		noImageLabel.setVisible(image == null);
		resourceViewer.setVisible(image != null);

		if (image != null && image.getWidth() > Constants.MAX_SIZE) {
			resourceViewer.setFitWidth(Constants.MAX_SIZE);
		} else {
			resourceViewer.setFitWidth(0);
		}
		resourceViewer.setImage(image);

	}

	private void handleDataListItemSelection() {
		final CacheFile cacheFile = dataList.getSelectionModel().getSelectedItem();
		final CacheFile detailedCacheFile = CacheFileBuilder.buildWithDetails(cacheFile);
		cacheImageViewer.setImage(null);
		cacheFileDescription.setText(detailedCacheFile.toString());

		if (detailedCacheFile instanceof MapGridFile) {
			final MapGridFile mapGridFile = (MapGridFile) detailedCacheFile;
			final Image image = renderTile(mapGridFile);
			cacheImageViewer.setImage(image);
		} else if (detailedCacheFile instanceof ResFile) {
			final ResFile resFile = (ResFile) detailedCacheFile;
			final Image image = resFile.getImage();
			cacheImageViewer.setImage(image);
		}
	}

	private Image renderTile(MapGridFile mapGridFile) {
		final byte[] content = mapGridFile.getContent();
		final List<BufferedImage> awtTileSet = mapGridFile.getTileSet()
				.stream()
				.map(tileInfo -> ImageCache.get().getResource(tileInfo.getResourcePath()))
				.collect(Collectors.toList());
		final WritableRaster raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, Constants.TILE_SIZE, Constants.TILE_SIZE, 4, null);

		for (int y = 1; y < Constants.TILE_SIZE - 1; y++) {
			for (int x = 1; x < Constants.TILE_SIZE - 1; x++) {
				final byte pixelIndex = content[y * Constants.TILE_SIZE + x];
				final BufferedImage texture = awtTileSet.get(pixelIndex);
				final int texturePixel = texture == null ? 0 : texture.getRGB(x, y);
				raster.setSample(x,y, 0, (texturePixel & 0x00ff0000) >>> 16);
				raster.setSample(x,y, 1, (texturePixel & 0x0000ff00) >>> 8);
				raster.setSample(x,y, 2, (texturePixel & 0x000000ff));
				raster.setSample(x,y, 3, (texturePixel & 0xff000000) >>> 24);
			}
		}

		final BufferedImage awtImage = new BufferedImage(Constants.COLOR_MODEL, raster, false, null);
		return SwingFXUtils.toFXImage(awtImage, null);


	}

}
