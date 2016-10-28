package com.evgenltd.mapper.ui.command.layer;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.bean.LayerBean;
import com.evgenltd.mapper.core.bean.Loader;
import com.evgenltd.mapper.core.bean.MarkerBean;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.component.command.Command;
import com.evgenltd.mapper.ui.component.command.CommandTemplate;
import com.evgenltd.mapper.ui.component.mapviewer.MapViewerWrapper;
import com.evgenltd.mapper.ui.node.LayerNode;
import com.evgenltd.mapper.ui.node.MarkerNode;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.List;
import java.util.Optional;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 06-07-2016 22:20
 */
@CommandTemplate(
		id = UIConstants.REMOVE,
		text = "Remove",
		longText = "Remove selected objects",
		graphic = "/image/bin-metal-full.png",
		accelerator = "Delete",
		path = "/Layer",
		position = 6
)
public class Remove extends Command {

	@Override
	protected void execute(ActionEvent event) {

		final MapViewerWrapper mapViewerWrapper = UIContext
				.get()
				.getMapViewerWrapper();

		final List<Long> selectedLayers = mapViewerWrapper.getSelectedNodeIds(LayerNode.class);
		final List<Long> selectedMarkers = mapViewerWrapper.getSelectedNodeIds(MarkerNode.class);

		if(selectedLayers.isEmpty() && selectedMarkers.isEmpty())	{
			return;
		}

		final LayerBean layerBean = Context.get().getLayerBean();
		final MarkerBean markerBean = Context.get().getMarkerBean();
		final Loader loader = Context.get().getLoader();

		Optional<Boolean> removeLinkedMarkers = Optional.empty();

		final boolean layersContainsLinkedMarkers = loader.isLayersContainsLinkedMarkers(selectedLayers);
		if(layersContainsLinkedMarkers)	{

			final Optional<Boolean> askResult = askDeleteLinkedMarkers();
			if(!askResult.isPresent())	{
				return;
			}
			removeLinkedMarkers = askResult;

		}

		markerBean.removeMarkersById(selectedMarkers);
		layerBean.removeLayers(selectedLayers, removeLinkedMarkers);

		UIContext.get().refresh();

	}

	private Optional<Boolean> askDeleteLinkedMarkers()	{
		final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setContentText("Selected layers contains linked markers. Would you like to delete them too?");
		alert.getButtonTypes().setAll(
				ButtonType.YES,
				ButtonType.NO,
				ButtonType.CANCEL
		);

		return alert
				.showAndWait()
				.map(buttonType -> {
					if(buttonType.equals(ButtonType.YES))	{
						return true;
					}else if(buttonType.equals(ButtonType.NO))	{
						return false;
					}else {
						return null;
					}
				});
	}
}
