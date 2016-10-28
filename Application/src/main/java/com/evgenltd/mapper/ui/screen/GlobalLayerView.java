package com.evgenltd.mapper.ui.screen;

import com.evgenltd.mapper.core.entity.dto.LayerDto;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.command.globallayer.*;
import com.evgenltd.mapper.ui.component.command.CommandManager;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;


/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 14-06-2016 03:01
 */
public class GlobalLayerView extends AbstractScreen	{

	@FXML private ImageView image;
	@FXML private Label name;
	@FXML private Label count;
	@FXML private ImageView visibility;
	@FXML private MenuButton menu;
	@FXML private MenuItem generateLevels;
	@FXML private MenuItem export;
	@FXML private MenuItem up;
	@FXML private MenuItem down;
	@FXML private MenuItem full;
	@FXML private MenuItem partly;
	@FXML private MenuItem none;

	private LayerDto layer;

	private final CommandManager commandManager = UIContext.get().getCommandManager();

	GlobalLayerView() {}

	public void setLayer(LayerDto layer) {

		this.layer = layer;

		if(layer.getType().isGround())	{
			image.setImage(UIConstants.MAP_ICON);
		}else if(layer.getType().isCave())	{
			image.setImage(UIConstants.CAVE_ICON);
		}

		name.setText(layer.getName());
		count.setText(String.format(
				"%s tiles",
				layer.getTileCount()
		));

		if(layer.getVisibility().isPartly())	{
			visibility.setImage(UIConstants.EYE_HALF);
		}else if(layer.getVisibility().isNone())	{
			visibility.setImage(UIConstants.EYE_CLOSE);
		}else {
			visibility.setImage(null);
		}

		initMenu();

	}

	private void initMenu() {

		menu.showingProperty().addListener((observable, oldValue, newValue) -> {
			if(!newValue){
				return;
			}
			((GenerateLevels)commandManager.getCommand(UIConstants.GLOBAL_LAYER_GENERATE_LEVELS)).setLayerId(layer.getId());
			((ExportToFolder)commandManager.getCommand(UIConstants.GLOBAL_EXPORT_LAYER_TO_FOLDER)).setLayerId(layer.getId());
			((ReorderUp)commandManager.getCommand(UIConstants.GLOBAL_LAYER_UP)).setLayerId(layer.getId());
			((ReorderDown)commandManager.getCommand(UIConstants.GLOBAL_LAYER_DOWN)).setLayerId(layer.getId());
			((VisibilityFull)commandManager.getCommand(UIConstants.GLOBAL_LAYER_FULL)).setLayerId(layer.getId());
			((VisibilityPartly)commandManager.getCommand(UIConstants.GLOBAL_LAYER_PARTLY)).setLayerId(layer.getId());
			((VisibilityNone)commandManager.getCommand(UIConstants.GLOBAL_LAYER_NONE)).setLayerId(layer.getId());
		});

		commandManager.configureMenuItem(UIConstants.GLOBAL_LAYER_GENERATE_LEVELS, generateLevels);
		commandManager.configureMenuItem(UIConstants.GLOBAL_EXPORT_LAYER_TO_FOLDER, export);
		commandManager.configureMenuItem(UIConstants.GLOBAL_LAYER_UP, up);
		commandManager.configureMenuItem(UIConstants.GLOBAL_LAYER_DOWN, down);
		commandManager.configureMenuItem(UIConstants.GLOBAL_LAYER_FULL, full);
		commandManager.configureMenuItem(UIConstants.GLOBAL_LAYER_PARTLY, partly);
		commandManager.configureMenuItem(UIConstants.GLOBAL_LAYER_NONE, none);

	}

}
