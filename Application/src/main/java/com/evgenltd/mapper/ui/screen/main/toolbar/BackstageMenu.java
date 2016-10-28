package com.evgenltd.mapper.ui.screen.main.toolbar;

import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.component.command.CommandManager;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 23-08-2016 22:29
 */
public class BackstageMenu {

	private final Toolbar toolbar;
	private final PopupControl holder = new PopupControl();
	private final TabPane tabPane = new TabPane();

	private final CommandManager commandManager = UIContext.get().getCommandManager();

	public BackstageMenu(@NotNull final Toolbar toolbar) {
		this.toolbar = toolbar;
		initUI();
	}

	private void initUI()	{

		final Label fileLabel = new Label("File");
		fileLabel.getStyleClass().add("file-tab-label");
		fileLabel.setOnMouseClicked(event -> hide());

		final Tab tab = new Tab();
		tab.setGraphic(fileLabel);
		tab.getStyleClass().add("file-tab");
		tab.setClosable(false);
		tab.setContent(createItems());

		tabPane.getTabs().add(tab);

		final VBox wrapper = new VBox(tabPane);
		wrapper.getStyleClass().setAll("backstage-popup");

		holder.getScene().setRoot(wrapper);
		holder.setAutoHide(true);

	}

	private VBox createItems()	{

		final Button undo = new Button();
		undo.getStyleClass().add("item");
		commandManager.configureButton(UIConstants.UNDO, undo);

		final Button redo = new Button();
		redo.getStyleClass().add("item");
		commandManager.configureButton(UIConstants.REDO, redo);

		final Button importOldVersion = new Button();
		importOldVersion.getStyleClass().add("item");
		commandManager.configureButton(UIConstants.IMPORT, importOldVersion);

		final Button settings = new Button();
		settings.getStyleClass().add("item");
		commandManager.configureButton(UIConstants.OPEN_SETTINGS_COMMAND, settings);

		final Button about = new Button();
		about.getStyleClass().add("item");
		commandManager.configureButton(UIConstants.ABOUT, about);

		final VBox container = new VBox();
		container.getChildren().addAll(
				undo,
				redo,
				new Separator(Orientation.HORIZONTAL),
				importOldVersion,
				new Separator(Orientation.HORIZONTAL),
				settings,
				new Separator(Orientation.HORIZONTAL),
				about
		);
		return container;

	}

	public void show()	{
		final Point2D position = toolbar.getRoot().localToScreen(0, 0);
		holder.show(UIContext.get().getStage(), position.getX(), position.getY());
	}

	private void hide()	{
		holder.hide();
	}

}
