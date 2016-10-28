package com.evgenltd.mapper.ui.screen.main.toolbar;

import com.evgenltd.mapper.ui.screen.AbstractScreen;
import javafx.event.Event;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.jetbrains.annotations.NotNull;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 13-06-2016 17:24
 */
public class Toolbar extends AbstractScreen{

	private final TabPane tabPane = new TabPane();
	private final HomeTab homeTab = new HomeTab();
	private final MarkerEditingTab markerEditingTab = new MarkerEditingTab();

	private final Tab fileTabHolder = new Tab();
	private final Tab homeTabHolder = new Tab("", homeTab.getRoot());
	private final Tab markerEditingTabHolder = new Tab("", markerEditingTab.getRoot());

	private final BackstageMenu backstageMenu = new BackstageMenu(this);

	public Toolbar() {
		initUI();
	}

	private void initUI()	{
		setRoot(tabPane);

		fileTabHolder.setClosable(false);
		fileTabHolder.getStyleClass().add("file-tab");
		styleTab(fileTabHolder, "File", "file-tab-label");

		homeTabHolder.setClosable(false);
		styleTab(homeTabHolder, "Home", "tab-label");

		markerEditingTabHolder.setClosable(false);
		styleTab(markerEditingTabHolder, "Marker Editing", "tab-label");

		tabPane.getTabs().addAll(fileTabHolder, homeTabHolder);
		tabPane.getSelectionModel().select(homeTabHolder);

		fileTabHolder.setOnSelectionChanged(this::handleFileTabSelectionChanged);
	}

	private void styleTab(@NotNull final Tab tab, @NotNull final String title, @NotNull final String style)	{
		final Label fileLabel = new Label(title);
		fileLabel.getStyleClass().add(style);
		tab.setGraphic(fileLabel);
	}

	public void showMarkerEditingTab()	{
		markerEditingTab.refresh();
		tabPane.getTabs().add(markerEditingTabHolder);
		tabPane.getSelectionModel().select(markerEditingTabHolder);
	}

	public void hideMarkerEditingTab()	{
		tabPane.getTabs().remove(markerEditingTabHolder);
		tabPane.getSelectionModel().select(homeTabHolder);
	}

	public void handleFileTabSelectionChanged(final Event event)	{
		tabPane.getSelectionModel().select(homeTabHolder);
		backstageMenu.show();
	}

}
