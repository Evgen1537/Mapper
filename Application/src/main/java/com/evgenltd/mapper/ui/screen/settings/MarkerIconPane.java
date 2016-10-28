package com.evgenltd.mapper.ui.screen.settings;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.bean.CommonDao;
import com.evgenltd.mapper.core.bean.Loader;
import com.evgenltd.mapper.core.bean.MarkerBean;
import com.evgenltd.mapper.core.entity.MarkerIcon;
import com.evgenltd.mapper.ui.cellfactory.MarkerIconListCell;
import com.evgenltd.mapper.ui.screen.AbstractScreen;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 18-06-2016 23:37
 */
public class MarkerIconPane extends AbstractScreen {

	@FXML private ListView<MarkerIcon> markerIconList;
	@FXML private Button markerIconRemove;

	private final CommonDao commonDao = Context.get().getCommonDao();
	private final Loader loader = Context.get().getLoader();
	private final MarkerBean markerBean = Context.get().getMarkerBean();

	private final MarkerIcon defaultMarkerIcon = loader.loadDefaultMarkerIcon();

	private final List<MarkerIcon> markerIconForDelete = new ArrayList<>();
	private final List<MarkerIcon> markerIconForAdd = new ArrayList<>();

	void initUI()	{
		markerIconList.setCellFactory(param -> new MarkerIconListCell());
		markerIconList.getSelectionModel().selectedItemProperty().addListener(observable -> updateMarkerIconButtons());
	}

	void fillUI()	{
		updateMarkerIconList();
		updateMarkerIconButtons();
	}

	void persist()	{
		markerIconForDelete.forEach(markerBean::removeMarkerIcon);
		markerIconForAdd.forEach(commonDao::insert);
	}

	// refreshing

	private void updateMarkerIconList()	{
		markerIconList.getItems().clear();
		loader
				.loadMarkerIconList()
				.stream()
				.filter(markerIcon -> !markerIconForDelete.contains(markerIcon))
				.collect(Collectors.toList())
				.forEach(markerIcon -> markerIconList.getItems().add(markerIcon));
		markerIconList.getItems().addAll(markerIconForAdd);
	}

	private void updateMarkerIconButtons()	{
		final MarkerIcon markerIconForCheck = markerIconList.getSelectionModel().getSelectedItem();
		final boolean isDisabled = markerIconList.getSelectionModel().isEmpty()
				|| Objects.equals(markerIconForCheck, defaultMarkerIcon);

		markerIconRemove.setDisable(isDisabled);
	}

	// fxml handlers

	@FXML
	private void handleAddMarkerIcon(ActionEvent event) {
		new MarkerIconAdd().showAndWait().ifPresent(markerIconForAdd::add);
		updateMarkerIconList();
	}

	@FXML
	private void handleRemoveMarkerIcon(ActionEvent event) {

		if(markerIconList.getSelectionModel().isEmpty())	{
			return;
		}

		final MarkerIcon selectedMarkerIcon = markerIconList.getSelectionModel().getSelectedItem();
		if(selectedMarkerIcon.getId() != null) {
			markerIconForDelete.add(selectedMarkerIcon);
		}
		updateMarkerIconList();

	}
}
