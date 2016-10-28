package com.evgenltd.mapper.ui.component.mapviewer;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.bean.CommonBean;
import com.evgenltd.mapper.core.bean.SettingsBean;
import com.evgenltd.mapper.core.entity.Layer;
import com.evgenltd.mapper.core.entity.Marker;
import com.evgenltd.mapper.core.util.Constants;
import com.evgenltd.mapper.mapviewer.common.MapViewer;
import com.evgenltd.mapper.mapviewer.common.MoveEvent;
import com.evgenltd.mapper.mapviewer.common.Node;
import com.evgenltd.mapper.mapviewer.common.ZLevel;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.component.selectiondispatcher.AgentItemImpl;
import com.evgenltd.mapper.ui.component.selectiondispatcher.SelectionDispatcher;
import com.evgenltd.mapper.ui.node.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.scene.control.ContextMenu;
import math.geom2d.Point2D;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 13-06-2016 18:36
 */
public class MapViewerWrapper implements SelectionDispatcher.Agent{

	private final MapViewer mapViewer = new MapViewer();

	private StringProperty markerSearchQuery = new SimpleStringProperty();
	public String getMarkerSearchQuery() {return markerSearchQuery.get();}
	public StringProperty markerSearchQueryProperty() {return markerSearchQuery;}
	public void setMarkerSearchQuery(String markerSearchQuery) {this.markerSearchQuery.set(markerSearchQuery);}

	private BooleanProperty markerOnlyVisibleOption = new SimpleBooleanProperty();
	public boolean getMarkerOnlyVisibleOption() {return markerOnlyVisibleOption.get();}
	public BooleanProperty markerOnlyVisibleOptionProperty() {return markerOnlyVisibleOption;}
	public void setMarkerOnlyVisibleOption(boolean markerOnlyVisibleOption) {this.markerOnlyVisibleOption.set(markerOnlyVisibleOption);}

	private Service<DataLoadingResult> loaderService = new MapViewerLoaderService(this);

	private final CommonBean commonBean = Context.get().getCommonBean();
	private final SettingsBean settingsBean = Context.get().getSettingsBean();

	public MapViewerWrapper() {
		mapViewer.setOnViewportChanged(event -> refresh());
		mapViewer.setOnMoved(this::handleNodeMoved);
		markerSearchQueryProperty().addListener(param -> loadData());
		markerOnlyVisibleOptionProperty().addListener(param -> loadData());
		mapViewer.moveToPoint(
				settingsBean.getViewportCentroidX(),
				settingsBean.getViewportCentroidY()
		);
		UIContext
				.get()
				.getSelectionDispatcher()
				.registerAgent(this);
	}

	public void stop()	{
		settingsBean.setViewportCentroidX(mapViewer.getViewportCentroidX());
		settingsBean.setViewportCentroidY(mapViewer.getViewportCentroidY());
	}

	public javafx.scene.Node getRoot()	{
		return mapViewer;
	}

	// delegates

	public void repaint()	{
		mapViewer.refresh();
	}

	public double getViewportWorldX()	{
		return mapViewer.getViewportX();
	}

	public double getViewportWorldY()	{
		return mapViewer.getViewportY();
	}

	public double getViewportWorldWidth()	{
		return mapViewer.getViewportWidth();
	}

	public double getViewportWorldHeight()	{
		return mapViewer.getViewportHeight();
	}

	ZLevel getViewportWorldLevel()	{
		return mapViewer.getViewportLevel();
	}

	public double getCursorWorldX() {
		return mapViewer.getCursorX();
	}

	public double getCursorWorldY() {
		return mapViewer.getCursorY();
	}

	void mergeNodes(@NotNull List<Node> nodeList) {
		nodeList.add(new GridNode());
		nodeList.add(new RulerNode());
		nodeList.add(new DebugInfoNode());
		mapViewer.mergeNodes(nodeList);
		UIContext.get().getCommandManager().updateCommandDisableState();
	}

	public void beginTargetingPoint(@NotNull Function<Point2D,Boolean> callback) {
		mapViewer.beginTargetingPoint(callback);
	}

	public boolean isTargetingPoint() {
		return mapViewer.isTargetingPoint();
	}

	public void beginEditingNode(@NotNull Node nodeForEditing) {
		mapViewer.beginEditingNode(nodeForEditing);
	}

	public void endEditingNode() {
		mapViewer.endEditingNode();
	}

	public void setMoveStep(final double step) {
		mapViewer.setMoveStep(step);
	}

	public void setContextMenu(@NotNull final ContextMenu contextMenu)	{
		mapViewer.setContextMenu(contextMenu);
	}

	public void moveViewportToPoint(final double worldX, final double worldY)	{
		mapViewer.moveToPoint(worldX, worldY);
	}

	// selection api

	@Override
	public void setSelectionListener(@NotNull Consumer<SelectionDispatcher.Agent> listener) {
		mapViewer.setOnSelectionChanged(event -> {
			setMoveStep(
					getSelectedNodeCount(LayerNode.class) == 0
							? 1
							: Constants.TILE_SIZE
			);
			listener.accept(this);
		});
	}

	@Override
	public void clearSelectionListener() {
		mapViewer.setOnSelectionChanged(null);
	}

	@Override
	public @NotNull List<SelectionDispatcher.AgentItem<?>> getSelectedItems() {
		return mapViewer
				.getSelectedNodes(this::isLayerOrMarker)
				.stream()
				.map(this::toAgentItem)
				.collect(Collectors.toList());
	}

	@Override
	public void setSelectedItems(@NotNull final List<SelectionDispatcher.AgentItem<?>> selectedItems) {
		final Predicate<Node> filter = node -> selectedItems
				.stream()
				.anyMatch(agentItem -> isEquals(node, agentItem));
		mapViewer.updateSelection(filter);
		setMoveStep(
				getSelectedNodeCount(LayerNode.class) == 0
						? 1
						: Constants.TILE_SIZE
		);
	}

	private boolean isLayerOrMarker(@NotNull final Node node)	{
		return node instanceof LayerNode || node instanceof MarkerNode;
	}

	private SelectionDispatcher.AgentItem<?> toAgentItem(@NotNull final Node node)	{
		if(node instanceof LayerNode)	{
			return new AgentItemImpl<>(node.getIdentifier(), Layer.class);
		}else if(node instanceof MarkerNode)	{
			return new AgentItemImpl<>(node.getIdentifier(), Marker.class);
		}else {
			throw new IllegalArgumentException(String.format("Not supported node type, class=[%s]",node.getClass()));
		}
	}

	private boolean isEquals(@NotNull final Node node, @NotNull final SelectionDispatcher.AgentItem agentItem)	{

		return Objects.equals(agentItem.getIdentifier(), node.getIdentifier())
				&& (
				(node instanceof LayerNode && agentItem.getType().equals(Layer.class))
						|| (node instanceof MarkerNode && agentItem.getType().equals(Marker.class))
		);

	}

	public List<Node> getSelectedNodes(@NotNull final Class<? extends Node> nodeClass)	{
		return mapViewer
				.getSelectedNodes(node -> nodeClass.isAssignableFrom(node.getClass()));
	}

	public List<Long> getSelectedNodeIds(@NotNull final Class<? extends Node> nodeClass)	{
		return getSelectedNodes(nodeClass)
				.stream()
				.map(Node::getIdentifier)
				.collect(Collectors.toList());
	}

	public int getSelectedNodeCount(final Class<? extends Node> nodeClass)	{
		return mapViewer
				.getSelectedNodes(node -> nodeClass == null || nodeClass.isAssignableFrom(node.getClass()))
				.size();
	}

	// load data

	public void refresh()	{
		loadData();
	}

	private void loadData()	{
		loaderService.restart();
	}

	// move persist

	private void handleNodeMoved(MoveEvent moveEvent)  {

		final List<Long> selectedLayerIdList = getSelectedNodeIds(LayerNode.class);
		final List<Long> selectedMarkerIdList = getSelectedNodes(MarkerNode.class)
				.stream()
				.filter(node -> {
					final MarkerNode markerNode = (MarkerNode)node;
					final Marker marker = markerNode.getMarker();
					return !selectedLayerIdList.contains(marker.getLayerId())
							&& !selectedLayerIdList.contains(marker.getExitId());
				})
				.map(Node::getIdentifier)
				.collect(Collectors.toList());

		commonBean.persistMovedState(
				selectedLayerIdList,
				selectedMarkerIdList,
				moveEvent.getDeltaX(),
				moveEvent.getDeltaY()
		);

		loadData();

	}
}
