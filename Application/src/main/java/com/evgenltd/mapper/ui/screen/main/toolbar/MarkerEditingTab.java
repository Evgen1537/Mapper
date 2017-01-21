package com.evgenltd.mapper.ui.screen.main.toolbar;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.bean.Loader;
import com.evgenltd.mapper.core.entity.Layer;
import com.evgenltd.mapper.core.entity.Marker;
import com.evgenltd.mapper.core.entity.MarkerIcon;
import com.evgenltd.mapper.core.entity.impl.EntityFactory;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.cellfactory.OptionalLayerListCell;
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
import java.util.Optional;
import java.util.stream.Collectors;

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

	@FXML private ComboBox<Optional<Layer>> layer;
	@FXML private ComboBox<Optional<Layer>> exit;
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
	private List<Optional<Layer>> layerList;

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
		layer.setButtonCell(new OptionalLayerListCell(true));
		layer.setCellFactory(param -> new OptionalLayerListCell(false));
		exit.setButtonCell(new OptionalLayerListCell(true));
		exit.setCellFactory(param -> new OptionalLayerListCell(false));
	}

	private void initBindings()	{
		essence.textProperty().addListener(param -> marker.setEssence(essence.getText()));
		substance.textProperty().addListener(param -> marker.setSubstance(substance.getText()));
		vitality.textProperty().addListener(param -> marker.setVitality(vitality.getText()));
		layer.getSelectionModel().selectedItemProperty().addListener(param -> marker.setLayer(layer.getSelectionModel().getSelectedItem().orElse(null)));
		exit.getSelectionModel().selectedItemProperty().addListener(param -> marker.setExit(exit.getSelectionModel().getSelectedItem().orElse(null)));
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
		layer.getSelectionModel().select(Optional.empty());
		exit.getSelectionModel().select(Optional.empty());
		comment.setText("");
	}

	//

	public void refresh()	{
		clearState();
		loadData();
	}

	private void loadData()	{
		this.marker = markerEditing.getMarker();
		layerList = loader
				.loadAllLayerList()
				.stream()
				.map(Optional::of)
				.collect(Collectors.toList());
		layerList.add(0,Optional.empty());
		fillUI();
	}

	private void fillUI()	{
		initMarkerIconMenu();

		layer.getItems().addAll(layerList);
		exit.getItems().addAll(layerList);

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
	private void handleLayerClear(ActionEvent event) {
		layer.getSelectionModel().select(Optional.empty());
	}

	@FXML
	private void handlerExitClear(ActionEvent event) {
		exit.getSelectionModel().select(Optional.empty());
	}

	private static void selectLayerById(@NotNull final ComboBox<Optional<Layer>> layerComboBox, @NotNull final Long id)	{

		final Optional<Layer> layerForSelection = layerComboBox
				.getItems()
				.stream()
				.filter(layer -> layer.isPresent()
						&& Objects.equals(layer.get().getId(), id))
				.findFirst()
				.orElse(Optional.empty());

		layerComboBox.getSelectionModel().select(layerForSelection);

	}
}
