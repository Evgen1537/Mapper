package com.evgenltd.mapper.ui.screen;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.bean.Loader;
import com.evgenltd.mapper.core.bean.TrackerBean;
import com.evgenltd.mapper.core.entity.Layer;
import com.evgenltd.mapper.core.entity.dto.LayerDto;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.cellfactory.LayerListCell;
import com.evgenltd.mapper.ui.component.command.CommandManager;
import com.evgenltd.mapper.ui.component.mapviewer.MapViewerWrapper;
import com.evgenltd.mapper.ui.component.selectiondispatcher.AgentItemImpl;
import com.evgenltd.mapper.ui.component.selectiondispatcher.SelectionDispatcher;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.beans.InvalidationListener;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 13-06-2016 18:25
 */
public class LayerBrowser extends AbstractScreen implements SelectionDispatcher.Agent {

	private final VBox content = new VBox();
	private final ListView<LayerDto> layerList = new ListView<>();
	private final List<GlobalLayerView> globalLayerViewList = new ArrayList<>();

	private final MapViewerWrapper mapViewerWrapper = UIContext.get().getMapViewerWrapper();
	private final SelectionDispatcher selectionDispatcher = UIContext.get().getSelectionDispatcher();

	private final Loader loader = Context.get().getLoader();
	private final TrackerBean trackerBean = Context.get().getTrackerBean();

	private InvalidationListener selectionListener;

	public LayerBrowser() {
		selectionDispatcher.registerAgent(this);
		initUI();
		loadData();
	}

	@Override
	public void refresh() {
		loadData();
	}

	@Override
	public void dispose() {
		selectionDispatcher.unregisterAgent(this);
	}

	private void initUI()	{
		globalLayerViewList.add(new GlobalLayerView());
		globalLayerViewList.add(new GlobalLayerView());
		globalLayerViewList.add(new GlobalLayerView());
		globalLayerViewList.add(new GlobalLayerView());
		globalLayerViewList.add(new GlobalLayerView());
		globalLayerViewList.add(new GlobalLayerView());

		globalLayerViewList.forEach(view -> content.getChildren().add(view.getRoot()));
		content.getChildren().add(layerList);
		content.setSpacing(4);
		content.setPadding(new Insets(4));
		setRoot(content);

		layerList.setCellFactory(param -> new LayerListCell(layer -> trackerBean.isLayerCurrent(layer.getId())));
		layerList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		layerList.setContextMenu(buildContextMenu());
	}

	private void loadData()	{
		final List<LayerDto> globalLayerList = loader.loadGlobalLayerList();
		final List<LayerDto> result = loader.loadSessionLayerList();

		for(int i=0; i<globalLayerList.size(); i++)	{
			final LayerDto layer = globalLayerList.get(i);
			final GlobalLayerView view = globalLayerViewList.get(i);
			view.setLayer(layer);
		}

		applyLoadedData(result);

//		fireSelectionChanged();
	}

	private void applyLoadedData(final List<LayerDto> loadedData)	{

		layerList.getSelectionModel().getSelectedItems().removeListener(selectionListener);

		final List<LayerDto> previouslySelected = layerList.getSelectionModel().getSelectedItems();
		layerList.getItems().setAll(loadedData);
		previouslySelected.forEach(layerDto -> layerList.getSelectionModel().select(layerDto));

		layerList.getSelectionModel().getSelectedItems().addListener(selectionListener);

	}

	// selection

	@Override
	public void setSelectionListener(@NotNull final Consumer<SelectionDispatcher.Agent> listener) {
		selectionListener = observable -> listener.accept(this);
		layerList
				.getSelectionModel()
				.getSelectedItems()
				.addListener(selectionListener);
	}

	@Override
	public void clearSelectionListener() {
		layerList
				.getSelectionModel()
				.getSelectedItems()
				.removeListener(selectionListener);
	}

	@Override
	public @NotNull List<SelectionDispatcher.AgentItem<?>> getSelectedItems() {
		return layerList
				.getSelectionModel()
				.getSelectedItems()
				.stream()
				.map(layerDto -> new AgentItemImpl<>(layerDto.getId(), Layer.class))
				.collect(Collectors.toList());
	}

	@Override
	public void setSelectedItems(@NotNull final List<SelectionDispatcher.AgentItem<?>> selectedItems) {
		final Predicate<LayerDto> containsIn = layerDto -> {
			final AgentItemImpl<Layer> agentItem = new AgentItemImpl<>(layerDto.getId(), Layer.class);
			return selectedItems.contains(agentItem);
		};
		layerList.getSelectionModel().clearSelection();
		layerList
				.getItems()
				.stream()
				.filter(containsIn)
				.forEach(layerDto -> layerList.getSelectionModel().select(layerDto));
	}

	// context menu

	private ContextMenu buildContextMenu()	{

		final CommandManager commandManager = UIContext
				.get()
				.getCommandManager();

		return new ContextMenu(
				commandManager.createMenuItem(UIConstants.GENERATE_LEVELS),
				commandManager.createMenuItem(UIConstants.REFRESH_LAYER),
				commandManager.createMenuItem(UIConstants.MERGE_WITH_GLOBAL),
				commandManager.createMenuItem(UIConstants.MERGE_TOGETHER),
				commandManager.createMenuItem(UIConstants.REMOVE),
				new SeparatorMenuItem(),
				new Menu(
						"Order",
						new ImageView(UIConstants.LAYERS_ARRANGE),
						commandManager.createMenuItem(UIConstants.BRING_LAYERS_TO_FRONT),
						commandManager.createMenuItem(UIConstants.SEND_LAYERS_TO_BACK),
						commandManager.createMenuItem(UIConstants.BRING_LAYERS_FORWARD),
						commandManager.createMenuItem(UIConstants.SEND_LAYERS_BACKWARD)
				),
				new Menu(
						"Visible",
						new ImageView(UIConstants.EYE),
						commandManager.createMenuItem(UIConstants.VISIBILITY_FULL),
						commandManager.createMenuItem(UIConstants.VISIBILITY_PARTLY),
						commandManager.createMenuItem(UIConstants.VISIBILITY_NONE)
				)
		);

	}

}
