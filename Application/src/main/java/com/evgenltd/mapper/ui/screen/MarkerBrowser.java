package com.evgenltd.mapper.ui.screen;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.bean.Loader;
import com.evgenltd.mapper.core.entity.Marker;
import com.evgenltd.mapper.core.entity.MarkerIcon;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.cellfactory.MarkerImageTableCell;
import com.evgenltd.mapper.ui.component.command.CommandManager;
import com.evgenltd.mapper.ui.component.mapviewer.MapViewerWrapper;
import com.evgenltd.mapper.ui.component.selectiondispatcher.AgentItemImpl;
import com.evgenltd.mapper.ui.component.selectiondispatcher.SelectionDispatcher;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 14-06-2016 03:05
 */
public class MarkerBrowser extends AbstractScreen implements SelectionDispatcher.Agent {

	@FXML private TextField searchField;
	@FXML private ToggleButton onlyVisibleSwitch;
	@FXML private TableView<Marker> markerList;
	@FXML private TableColumn<Marker,Image> iconColumn;
	@FXML private TableColumn<Marker,String> nameColumn;
	@FXML private TableColumn<Marker,String> essenceColumn;
	@FXML private TableColumn<Marker,String> substanceColumn;
	@FXML private TableColumn<Marker,String> vitalityColumn;

	private final Loader loader = Context.get().getLoader();
	private final MapViewerWrapper mapViewerWrapper = UIContext.get().getMapViewerWrapper();
	private final SelectionDispatcher selectionDispatcher = UIContext.get().getSelectionDispatcher();

	private InvalidationListener selectionListener;

	public MarkerBrowser() {
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
		Bindings.unbindBidirectional(searchField.textProperty(),mapViewerWrapper.markerSearchQueryProperty());
		Bindings.unbindBidirectional(onlyVisibleSwitch.selectedProperty(), mapViewerWrapper.markerOnlyVisibleOptionProperty());
	}

	private void initUI()	{

		mapViewerWrapper.markerSearchQueryProperty().bind(searchField.textProperty());
		mapViewerWrapper.markerOnlyVisibleOptionProperty().bind(onlyVisibleSwitch.selectedProperty());

		searchField.textProperty().addListener(param -> loadData());
		onlyVisibleSwitch.selectedProperty().addListener(param -> loadData());

		markerList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		markerList.widthProperty().addListener(this::handleMarkerListWidthChanged);
		markerList.setContextMenu(buildContextMenu());

		iconColumn.setCellValueFactory(this::imageColumnCellValueFactory);
		iconColumn.setCellFactory(param -> new MarkerImageTableCell());
		nameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getName()));
		essenceColumn.setCellValueFactory(new PropertyValueFactory<>("essence"));
		substanceColumn.setCellValueFactory(new PropertyValueFactory<>("substance"));
		vitalityColumn.setCellValueFactory(new PropertyValueFactory<>("vitality"));

	}

	private void loadData()	{
		final List<Marker> loadedMarkerList = onlyVisibleSwitch.isSelected()
				? loader.loadMarkerListByViewportAndQuery(
						mapViewerWrapper.getViewportWorldX(),
						mapViewerWrapper.getViewportWorldY(),
						mapViewerWrapper.getViewportWorldX() + mapViewerWrapper.getViewportWorldWidth(),
						mapViewerWrapper.getViewportWorldY() + mapViewerWrapper.getViewportWorldHeight(),
						searchField.getText()
				)
				: loader.loadMarkerListByQuery(searchField.getText());

		applyLoadedData(loadedMarkerList);
//		fireSelectionChanged();
	}

	private void applyLoadedData(final List<Marker> loadedData)	{

		markerList.getSelectionModel().getSelectedItems().removeListener(selectionListener);

		final List<Marker> previouslySelected = markerList.getSelectionModel().getSelectedItems();
		markerList.getItems().setAll(loadedData);
		previouslySelected.forEach(marker -> markerList.getSelectionModel().select(marker));

		markerList.getSelectionModel().getSelectedItems().addListener(selectionListener);

	}

	// selection implementation

	@Override
	public void setSelectionListener(@NotNull final Consumer<SelectionDispatcher.Agent> listener) {
		selectionListener = observable -> listener.accept(this);
		markerList
				.getSelectionModel()
				.getSelectedItems()
				.addListener(selectionListener);
	}

	@Override
	public void clearSelectionListener() {
		markerList
				.getSelectionModel()
				.getSelectedItems()
				.removeListener(selectionListener);
	}

	@Override
	public @NotNull List<SelectionDispatcher.AgentItem<?>> getSelectedItems() {
		return markerList
				.getSelectionModel()
				.getSelectedItems()
				.stream()
				.map(marker -> new AgentItemImpl<>(marker.getId(), Marker.class))
				.collect(Collectors.toList());
	}

	@Override
	public void setSelectedItems(@NotNull List<SelectionDispatcher.AgentItem<?>> selectedItems) {
		final Predicate<Marker> containsIn = marker -> {
			final AgentItemImpl<Marker> agentItem = new AgentItemImpl<>(marker.getId(), Marker.class);
			return selectedItems.contains(agentItem);
		};
		markerList.getSelectionModel().clearSelection();
		markerList
				.getItems()
				.stream()
				.filter(containsIn)
				.forEach(marker -> markerList.getSelectionModel().select(marker));
	}

	// service

	private ObservableValue<Image> imageColumnCellValueFactory(final TableColumn.CellDataFeatures<Marker,Image> feature)	{
		final MarkerIcon icon = feature.getValue().getMarkerIcon();
		return icon == null
				? new ReadOnlyObjectWrapper<>(null)
				: new ReadOnlyObjectWrapper<>(icon.getImage().getImage());
	}

	private void handleMarkerListWidthChanged(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		double newWidth = newValue.doubleValue() - iconColumn.getWidth() - essenceColumn.getWidth() - substanceColumn.getWidth() - vitalityColumn.getWidth();
		nameColumn.setMinWidth(newWidth);
	}

	private ContextMenu buildContextMenu()	{

		final CommandManager commandManager = UIContext
				.get()
				.getCommandManager();

		return new ContextMenu(
				commandManager.createMenuItem(UIConstants.MARKER_EDIT_BEGIN),
				commandManager.createMenuItem(UIConstants.REMOVE)
		);
	}
}
