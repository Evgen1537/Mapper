package com.evgenltd.mapper.ui.screen.main.toolbar;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.bean.Loader;
import com.evgenltd.mapper.core.entity.Layer;
import com.evgenltd.mapper.core.entity.Marker;
import com.evgenltd.mapper.core.entity.MarkerIcon;
import com.evgenltd.mapper.core.entity.impl.EntityFactory;
import com.evgenltd.mapper.core.util.Constants;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.cellfactory.LayerListCell;
import com.evgenltd.mapper.ui.component.command.CommandManager;
import com.evgenltd.mapper.ui.component.markerediting.MarkerEditing;
import com.evgenltd.mapper.ui.screen.AbstractScreen;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 27-06-2016 21:01
 */
public class MarkerEditingTab extends AbstractScreen {

	@FXML private MenuButton markerIcon;
	@FXML private TextField essence;
	@FXML private TextField substance;
	@FXML private TextField vitality;

	@FXML private ComboBox<Layer> layer;
	@FXML private ComboBox<Layer> exit;
	@FXML private Button exitClear;

	@FXML private TextArea comment;

	@FXML private Button addPoint;
	@FXML private Button removePoint;

	@FXML private Button apply;
	@FXML private Button dismiss;

	private final Loader loader = Context.get().getLoader();
	private final CommandManager commandManager = UIContext.get().getCommandManager();
	private final MarkerEditing markerEditing = UIContext.get().getMarkerEditing();

	private Marker marker;
	private List<Layer> layerList;

	public MarkerEditingTab()	{
		configureCommandButtons();
		initMarkerIconMenu();
		initLayerComboBox();
		initBindings();
		clearState();
	}

	// init

	private void configureCommandButtons()	{
		commandManager.configureButton(UIConstants.MARKER_EDIT_APPLY, apply);
		commandManager.configureButton(UIConstants.MARKER_EDIT_CANCEL, dismiss);

		commandManager.configureButton(UIConstants.MARKER_EDIT_ADD_POINT, addPoint);
		commandManager.configureButton(UIConstants.MARKER_EDIT_REMOVE_POINT, removePoint);
	}

	private void initMarkerIconMenu()	{
		markerIcon.getItems().clear();
		final List<MarkerIcon> markerIconList = loader.loadMarkerIconList();
		for(MarkerIcon icon : markerIconList) {

			final ImageView iconView = new ImageView(icon.getImage().getImage());
			final MenuItem menuItem = new MenuItem(icon.getName(), iconView);

			menuItem.setOnAction(event -> {
				markerIcon.setGraphic(new ImageView(icon.getImage().getImage()));
				marker.setMarkerIcon(icon);
			});

			markerIcon.getItems().add(menuItem);

		}
	}

	private void initLayerComboBox()	{
		layer.setButtonCell(new LayerListCell(true));
		layer.setCellFactory(param -> new LayerListCell(false));
		exit.setButtonCell(new LayerListCell(true));
		exit.setCellFactory(param -> new LayerListCell(false));
	}

	private void initBindings()	{
		essence.textProperty().addListener(param -> marker.setEssence(essence.getText()));
		substance.textProperty().addListener(param -> marker.setSubstance(substance.getText()));
		vitality.textProperty().addListener(param -> marker.setVitality(vitality.getText()));
		layer.getSelectionModel().selectedItemProperty().addListener(param -> handleLayerSelection(layer, marker::setLayer));
		exit.getSelectionModel().selectedItemProperty().addListener(param -> handleLayerSelection(exit, marker::setExit));
		comment.textProperty().addListener(param -> marker.setComment(comment.getText()));
	}

	private void clearState()	{
		marker = EntityFactory.createMarker();

		markerIcon.setDisable(true);
		essence.setDisable(true);
		substance.setDisable(true);
		vitality.setDisable(true);
		exit.setDisable(true);
		exitClear.setDisable(true);

		markerIcon.setGraphic(null);
		essence.setText("");
		substance.setText("");
		vitality.setText("");
		layer.getSelectionModel().select(Constants.NONE);
		exit.getSelectionModel().select(Constants.NONE);
		comment.setText("");
	}

	//

	public void refresh()	{
		clearState();
		loadData();
	}

	private void loadData()	{
		this.marker = markerEditing.getMarker();
		layerList = loader.loadAllLayerList();
		layerList.add(0,Constants.NONE);
		fillUI();
	}

	private void fillUI()	{
		initMarkerIconMenu();

		layer.getItems().setAll(layerList);
		exit.getItems().setAll(layerList);

		if(marker.getType().isArea()) {
			final Image markerIconImage = marker.getMarkerIcon().getImage().getImage();
			markerIcon.setGraphic(new ImageView(markerIconImage));
			markerIcon.setDisable(false);

			essence.setText(marker.getEssence());
			substance.setText(marker.getSubstance());
			vitality.setText(marker.getVitality());
			essence.setDisable(false);
			substance.setDisable(false);
			vitality.setDisable(false);
		}

		if(marker.getLayer() != null) {
			selectLayerById(layer, marker.getLayer().getId());
		}

		if(marker.getType().isEntrance()) {
			exit.setDisable(false);
			exitClear.setDisable(false);
			if(marker.getExit() != null) {
				selectLayerById(exit, marker.getExit().getId());
			}
		}

		comment.setText(marker.getComment());
	}

	@FXML
	@SuppressWarnings("unused")
	private void handleLayerClear(ActionEvent event) {
		layer.getSelectionModel().select(Constants.NONE);
	}

	@FXML
	@SuppressWarnings("unused")
	private void handlerExitClear(ActionEvent event) {
		exit.getSelectionModel().select(Constants.NONE);
	}

	private static void selectLayerById(@NotNull final ComboBox<Layer> layerComboBox, @NotNull final Long id)	{

		final Layer layerForSelection = layerComboBox
				.getItems()
				.stream()
				.filter(layer -> Objects.equals(layer.getId(), id))
				.findFirst()
				.orElse(Constants.NONE);

		layerComboBox.getSelectionModel().select(layerForSelection);

	}

	private void handleLayerSelection(@NotNull final ComboBox<Layer> layerComboBox, @NotNull final Consumer<Layer> setter) {

		final Layer selectedLayer = layerComboBox.getSelectionModel().getSelectedItem();
		if (Objects.equals(selectedLayer, Constants.NONE)) {
			setter.accept(null);
			return;
		}

		setter.accept(selectedLayer);

	}
}
