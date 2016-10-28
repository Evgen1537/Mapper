package com.evgenltd.mapper.ui.component.command;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.bean.SettingsBean;
import com.evgenltd.mapper.core.bean.TrackerBean;
import com.evgenltd.mapper.core.bean.envers.EnversBean;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.component.command.scope.Default;
import com.evgenltd.mapper.ui.component.command.scope.MarkerEdit;
import com.evgenltd.mapper.ui.component.mapviewer.MapViewerWrapper;
import com.evgenltd.mapper.ui.component.markerediting.MarkerEditing;
import com.evgenltd.mapper.ui.component.selectiondispatcher.SelectionDispatcher;
import com.evgenltd.mapper.ui.node.LayerNode;
import com.evgenltd.mapper.ui.node.MarkerNode;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.application.Platform;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 18-06-2016 18:57
 */
public class CommandManager implements SelectionDispatcher.Agent {

	private static final String COMMAND_CLASSES_PACKAGE = "com.evgenltd.mapper.ui.command";
	private static final Comparator<Command> COMMAND_COMPARATOR = (o1, o2) -> {
		final int pathComparing = o1.getPath().compareTo(o2.getPath());
		if(pathComparing != 0)	{
			return pathComparing;
		}
		return o1.getText().compareTo(o2.getText());
	};

	private final Map<String,Command> commandMap = new HashMap<>();

	private final EnversBean enversBean = Context.get().getEnversBean();
	private final SettingsBean settingsBean = Context.get().getSettingsBean();
	private final TrackerBean trackerBean = Context.get().getTrackerBean();

	private final MapViewerWrapper mapViewerWrapper = UIContext.get().getMapViewerWrapper();
	private final MarkerEditing markerEditing = UIContext.get().getMarkerEditing();
	private final SelectionDispatcher selectionDispatcher = UIContext.get().getSelectionDispatcher();

	private Class<?> currentScope = Default.class;

	public void initCommands()	{
		selectionDispatcher.registerAgent(this);
		scanForCommands();
		initHotKey();
		mapViewerWrapper.setContextMenu(buildContextMenu());
		trackerBean.setEnableStateListener(state -> Platform.runLater(this::updateCommandDisableState));
	}

	@NotNull
	public Command getCommand(@NotNull final String commandId)	{
		final Command command = commandMap.get(commandId);
		if(command == null)	{
			throw new IllegalStateException(String.format("Command with id=[%s] does not exists", commandId));
		}
		return command;
	}

	public List<Command> getCommandList()	{
		final List<Command> result = commandMap
				.values()
				.stream()
				.filter(command -> !command.getPath().isEmpty())
				.collect(Collectors.toList());
		Collections.sort(result, COMMAND_COMPARATOR);
		return result;
	}

	//

	private void scanForCommands()	{
		final List<Class<?>> commandClasses = CommandScanner.scanClasses(COMMAND_CLASSES_PACKAGE);
		commandClasses.forEach(this::registerCommand);
//		fireSelectionChanged();

	}

	private void registerCommand(@NotNull final Class<?> commandClass)	{
		final CommandTemplate commandTemplate = commandClass.getAnnotation(CommandTemplate.class);
		if(commandTemplate == null)	{
			return;
		}

		final Command command = instantiateCommand(commandClass, commandTemplate);
		commandMap.put(commandTemplate.id(), command);
	}

	private Command instantiateCommand(final Class<?> commandClass, final CommandTemplate commandTemplate)	{
		final Command command = newInstance(commandClass);
		command.setId(commandTemplate.id());

		if(checkString(commandTemplate.text())) {
			command.setText(commandTemplate.text());
		}

		if(checkString(commandTemplate.longText())) {
			command.setLongText(commandTemplate.longText());
			command.setLongTextTemplate(commandTemplate.longText());
		}

		if(checkString(commandTemplate.accelerator())) {
			command.setAccelerator(KeyCombination.valueOf(commandTemplate.accelerator()));
		}

		if(checkString(commandTemplate.graphic())) {
			final Node graphic = makeCommandGraphic(commandTemplate.graphic());
			command.setGraphic(graphic);
		}

		command.setPath(commandTemplate.path());
		command.setPosition(commandTemplate.position());
		command.setScope(commandTemplate.scope());

		return command;
	}

	//

	private void initHotKey()	{
		updateHotKeysInSettings();
		updateHotKeysFromSettings();
	}

	/**
	 * Updating hot keys in application settings for first launch.
	 * Performs only if settings created on launching application
	 */
	private void updateHotKeysInSettings()	{

		commandMap.forEach((commandId, command) -> {

			if(command.getAccelerator() == null)	{
				return;
			}
			settingsBean.putHotKey(commandId, command.getAccelerator());

		});

	}

	/**
	 * Regular updating hot keys from application settings.
	 * User through the UI form can change settings,
	 * and after saving hot key map in CommandManager should be updated
	 */
	public void updateHotKeysFromSettings()	{
		updateHotKeysFromSettings(Default.class);
	}

	public void updateHotKeysFromSettings(@NotNull final Class<?> scope)	{

		Platform.runLater(() -> {

			currentScope = scope;

			final ObservableMap<KeyCombination,Runnable> globalAcceleratorsMap = UIContext
					.get()
					.getStage()
					.getScene()
					.getAccelerators();
			globalAcceleratorsMap.clear();

			commandMap.forEach((commandId, command) -> {

				if(!Objects.equals(command.getScope(), scope))	{
					return;
				}

				final KeyCombination hotKey = settingsBean.getHotKey(commandId);

				command.setAccelerator(hotKey);
				command.updateLongText();

				if(hotKey == null)	{
					return;
				}
				globalAcceleratorsMap.put(hotKey, () -> command.handle(new ActionEvent()));

			});

			updateCommandDisableState();

		});
	}

	// command state

	public void updateCommandDisableState()	{
		updateRegularCommands();
		updateSelectionDependCommands();
	}

	public void blockAllCommands()	{
		commandMap.forEach((s, command) -> command.setDisabled(true));
	}

	public void blockCommands(Class<?> scope)	{
		commandMap
				.values()
				.stream()
				.filter(command -> command.getScope().equals(scope))
				.forEach(command -> command.setDisabled(true));
	}

	private void updateRegularCommands()	{
		final boolean isDefaultScope = currentScope.equals(Default.class);
		final boolean isMarkerEditScope = currentScope.equals(MarkerEdit.class);

		getCommand(UIConstants.ADD_LAYER_FROM_FILE_SYSTEM).setDisabled(!isDefaultScope);
		getCommand(UIConstants.ADD_MANY_LAYERS_FROM_FILE_SYSTEM).setDisabled(!isDefaultScope);

		getCommand(UIConstants.OPEN_SETTINGS_COMMAND).setDisabled(!isDefaultScope);
		getCommand(UIConstants.IMPORT).setDisabled(!isDefaultScope);
		getCommand(UIConstants.ABOUT).setDisabled(!isDefaultScope);

		getCommand(UIConstants.MARKER_ADD_POINT).setDisabled(!isDefaultScope);
		getCommand(UIConstants.MARKER_ADD_AREA).setDisabled(!isDefaultScope);
		getCommand(UIConstants.MARKER_ADD_TRACK).setDisabled(!isDefaultScope);
		getCommand(UIConstants.MARKER_ADD_ENTRANCE).setDisabled(!isDefaultScope);

		getCommand(UIConstants.MARKER_HIDE).setDisabled(!isDefaultScope);
		getCommand(UIConstants.SELECTION_ALL).setDisabled(!isDefaultScope);
		getCommand(UIConstants.SELECTION_LAYER).setDisabled(!isDefaultScope);
		getCommand(UIConstants.SELECTION_MARKER).setDisabled(!isDefaultScope);

		getCommand(UIConstants.MARKER_EDIT_CANCEL).setDisabled(!isMarkerEditScope);
		getCommand(UIConstants.MARKER_EDIT_APPLY).setDisabled(!isMarkerEditScope);

		getCommand(UIConstants.START_TRACKING).setSelected(Context.get().getSettingsBean().isEnableTracker());
		getCommand(UIConstants.PAUSE_TRACKING).setSelected(!Context.get().getSettingsBean().isEnableTracker());
		getCommand(UIConstants.TO_PLAYER_POSITION).setDisabled(!isDefaultScope);

		final int tasksCount = UIContext.get().getTaskList().size();
		getCommand(UIConstants.UNDO).setDisabled(tasksCount > 0 || !enversBean.isUndoAvailable());
		getCommand(UIConstants.REDO).setDisabled(tasksCount > 0 || !enversBean.isRedoAvailable());
	}

	private void updateSelectionDependCommands()	{
		final boolean isDefaultScope = currentScope.equals(Default.class);
		final int selectedLayers = mapViewerWrapper.getSelectedNodeCount(LayerNode.class);
		getCommand(UIConstants.GENERATE_LEVELS).setDisabled(!isDefaultScope || selectedLayers == 0);
		getCommand(UIConstants.REFRESH_LAYER).setDisabled(!isDefaultScope || selectedLayers == 0);
		getCommand(UIConstants.FIND_MATCHES).setDisabled(!isDefaultScope || selectedLayers == 0);
		getCommand(UIConstants.EXPORT_LAYER_TO_FOLDER).setDisabled(!isDefaultScope || selectedLayers == 0);
		getCommand(UIConstants.MERGE_WITH_GLOBAL).setDisabled(!isDefaultScope || selectedLayers == 0);
		getCommand(UIConstants.MERGE_TOGETHER).setDisabled(!isDefaultScope || selectedLayers < 2);
		getCommand(UIConstants.BRING_LAYERS_TO_FRONT).setDisabled(!isDefaultScope || selectedLayers == 0);
		getCommand(UIConstants.BRING_LAYERS_FORWARD).setDisabled(!isDefaultScope || selectedLayers == 0);
		getCommand(UIConstants.SEND_LAYERS_BACKWARD).setDisabled(!isDefaultScope || selectedLayers == 0);
		getCommand(UIConstants.SEND_LAYERS_TO_BACK).setDisabled(!isDefaultScope || selectedLayers == 0);
		getCommand(UIConstants.VISIBILITY_FULL).setDisabled(!isDefaultScope || selectedLayers == 0);
		getCommand(UIConstants.VISIBILITY_PARTLY).setDisabled(!isDefaultScope || selectedLayers == 0);
		getCommand(UIConstants.VISIBILITY_NONE).setDisabled(!isDefaultScope || selectedLayers == 0);

		final int selectedMarkers = mapViewerWrapper.getSelectedNodeCount(MarkerNode.class);
		getCommand(UIConstants.MARKER_EDIT_BEGIN).setDisabled(!isDefaultScope || selectedMarkers != 1);

		getCommand(UIConstants.REMOVE).setDisabled(!isDefaultScope || (selectedLayers == 0 && selectedMarkers == 0));

		final long selectedPoints = markerEditing.getSelectedPointCount();
		final boolean markerPointAvailable = Optional
				.ofNullable(UIContext
									.get()
									.getMarkerEditing()
									.getMarker()
				)
				.map(marker -> !marker.getType().isEntrance())
				.orElse(false);
		getCommand(UIConstants.MARKER_EDIT_ADD_POINT).setDisabled(!markerPointAvailable);
		getCommand(UIConstants.MARKER_EDIT_REMOVE_POINT).setDisabled(!markerPointAvailable || selectedPoints == 0);
	}

	// configure

	public void configureButton(@NotNull final String commandId, @NotNull final ButtonBase buttonBase) {
		configureButton(commandId, buttonBase, ActionUtils.ActionTextBehavior.SHOW);
	}

	public void configureButton(
			@NotNull final String commandId,
			@NotNull final ButtonBase buttonBase,
			@NotNull final ActionUtils.ActionTextBehavior textBehavior
	) {
		final Action command = getCommand(commandId);
		ActionUtils.configureButton(command, buttonBase);
		if(Objects.equals(textBehavior, ActionUtils.ActionTextBehavior.HIDE))	{
			buttonBase.textProperty().unbind();
			buttonBase.setText("");
		}
	}

	public void configureMenuItem(@NotNull final String commandId, @NotNull MenuItem menuItem) {
		final Action command = getCommand(commandId);
		ActionUtils.configureMenuItem(command, menuItem);
	}

	public MenuItem createMenuItem(@NotNull final String commandId)	{
		final Action command = getCommand(commandId);
		return ActionUtils.createMenuItem(command);
	}

	// selection agent impl


	@Override
	public void setSelectionListener(@NotNull Consumer<SelectionDispatcher.Agent> listener) {}

	@Override
	public void clearSelectionListener() {}

	@Override
	public @NotNull List<SelectionDispatcher.AgentItem<?>> getSelectedItems() {
		return Collections.emptyList();
	}

	@Override
	public void setSelectedItems(@NotNull List<SelectionDispatcher.AgentItem<?>> selectedItems) {
		updateSelectionDependCommands();
	}

	//

	private ContextMenu buildContextMenu()	{
		return new ContextMenu(
				createMenuItem(UIConstants.GENERATE_LEVELS),
				createMenuItem(UIConstants.REFRESH_LAYER),
				createMenuItem(UIConstants.MERGE_WITH_GLOBAL),
				createMenuItem(UIConstants.MERGE_TOGETHER),
				createMenuItem(UIConstants.MARKER_EDIT_BEGIN),
				createMenuItem(UIConstants.REMOVE),
				new SeparatorMenuItem(),
				new Menu(
						"Order",
						new ImageView(UIConstants.LAYERS_ARRANGE),
						createMenuItem(UIConstants.BRING_LAYERS_TO_FRONT),
						createMenuItem(UIConstants.SEND_LAYERS_TO_BACK),
						createMenuItem(UIConstants.BRING_LAYERS_FORWARD),
						createMenuItem(UIConstants.SEND_LAYERS_BACKWARD)
				),
				new Menu(
						"Visible",
						new ImageView(UIConstants.EYE),
						createMenuItem(UIConstants.VISIBILITY_FULL),
						createMenuItem(UIConstants.VISIBILITY_PARTLY),
						createMenuItem(UIConstants.VISIBILITY_NONE)
				)
		);
	}

	//

	private static boolean checkString(@NotNull final String value)	{
		return !value.trim().isEmpty();
	}

	@NotNull
	private static Node makeCommandGraphic(@NotNull final String path)	{
		final Image image = new Image(path);
		return new ImageView(image);
	}

	@NotNull
	@SuppressWarnings("unchecked")
	private static <T> T newInstance(@NotNull final Class<?> type)	{
		try {
			return (T)type.newInstance();
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}


}
