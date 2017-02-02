package com.evgenltd.mapper.ui;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.ui.component.command.CommandManager;
import com.evgenltd.mapper.ui.component.eventlog.EventLog;
import com.evgenltd.mapper.ui.component.globalmap.GlobalMapModel;
import com.evgenltd.mapper.ui.component.mapviewer.MapViewerWrapper;
import com.evgenltd.mapper.ui.component.markerediting.MarkerEditing;
import com.evgenltd.mapper.ui.component.selectiondispatcher.SelectionDispatcher;
import com.evgenltd.mapper.ui.screen.AbstractScreen;
import com.evgenltd.mapper.ui.screen.dock.DockPane;
import com.evgenltd.mapper.ui.screen.main.Main;
import com.evgenltd.mapper.ui.screen.main.toolbar.Toolbar;
import com.evgenltd.mapper.ui.util.UIConstants;
import com.evgenltd.mapper.ui.util.UIExceptionHandler;
import com.evgenltd.mapper.ui.util.UpdateChecker;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 12-06-2016 23:52
 */
public class UIContext {
	private static UIContext instance = new UIContext();

	private HostServices hostServices;
	private Stage stage;

	private final Pane root = new StackPane();
	private Toolbar toolbar;
	private DockPane dockPane;

	private CommandManager commandManager;
	private MapViewerWrapper mapViewerWrapper;
	private EventLog eventLog;
	private SelectionDispatcher selectionDispatcher;
	private MarkerEditing markerEditing;
	private GlobalMapModel globalMapModel;

	private boolean debugMode;
	private final Map<String,String> debugInfo = new HashMap<>();

	private final ObservableMap<String,Task<?>> taskList = FXCollections.observableHashMap();

	// instantiation

	private UIContext() {}

	public static UIContext get() {
		return instance;
	}

	// lifecycle

	public void initWithDebugMode(@NotNull final Stage stage, @NotNull final HostServices hostServices)	{
		debugMode = true;
		init(stage, hostServices);
	}

	public void init(@NotNull final Stage stage, @NotNull final HostServices hostServices) {

		this.stage = stage;
		this.hostServices = hostServices;

		final Scene scene = new Scene(root,800,600);
		scene.getStylesheets().addAll("/css/mapper.css", "/css/ribbon.css");

		stage.setScene(scene);

		getCommandManager().initCommands();

		stage.setTitle(String.format(
				"%s %s",
				getClass().getPackage().getImplementationTitle(),
				getClass().getPackage().getImplementationVersion()
		));
		stage.getIcons().add(UIConstants.APP_ICON_16);
		stage.getIcons().add(UIConstants.APP_ICON_32);
		stage.getIcons().add(UIConstants.APP_ICON_64);
		stage.getIcons().add(UIConstants.APP_ICON_128);

		stage.show();

		Thread.currentThread().setUncaughtExceptionHandler(new UIExceptionHandler());

		openScreen(new Main());

		Context.get().getTrackerBean().setInvocationListener(() -> Platform.runLater(this::refresh));

		UpdateChecker.of().checkWithNotification();

	}

	public void refresh()	{
		mapViewerWrapper.refresh();
		dockPane.refresh();
	}

	public void close() {
		mapViewerWrapper.stop();
	}

	//

	public Stage getStage() {
		return stage;
	}

	public HostServices getHostServices() {
		return hostServices;
	}

	// screens

	public void openScreen(final AbstractScreen screen)	{
		root.getChildren().setAll(screen.getRoot());
	}

	// tasks

	public void submit(@NotNull final String commandId, @NotNull final Task<?> task)	{
		final Task<?> taskInProgress = taskList.remove(commandId);
		if (taskInProgress != null)	{
			taskInProgress.cancel();
		}
		taskList.put(commandId, task);
		task.stateProperty().addListener(observable -> taskMaintenance(task));
		Context.get().getCoreExecutor().execute(task);
	}

	public ObservableMap<String,Task<?>> getTaskList() {
		return FXCollections.unmodifiableObservableMap(taskList);
	}

	public void addTaskListChangedListener(@NotNull final Runnable runnable)	{
		taskList.addListener((InvalidationListener)observable -> runnable.run());
	}

	private void taskMaintenance(final Task<?> task)	{

		if(!task.isDone())	{
			return;
		}

		taskList
				.entrySet()
				.stream()
				.filter(entry -> entry.getValue().isDone())
				.map(Map.Entry::getKey)
				.collect(Collectors.toList())
				.forEach(taskList::remove);

	}

	//

	public boolean isDebugMode() {
		return debugMode;
	}

	public void putDebugInfo(@NotNull final String key, @NotNull final String value)	{
		debugInfo.put(key,value);
	}

	public Map<String,String> getAllDebugInfo()	{
		return Collections.unmodifiableMap(debugInfo);
	}

	// ui components

	public Toolbar getToolbar() {
		if(toolbar == null)	{
			toolbar = new Toolbar();
		}
		return toolbar;
	}

	public DockPane getDockPane() {
		if(dockPane == null)	{
			dockPane = new DockPane();
		}
		return dockPane;
	}

	public CommandManager getCommandManager() {
		if(commandManager == null)	{
			commandManager = new CommandManager();
		}
		return commandManager;
	}

	public MapViewerWrapper getMapViewerWrapper() {
		if(mapViewerWrapper == null)	{
			mapViewerWrapper = new MapViewerWrapper();
		}
		return mapViewerWrapper;
	}

	public EventLog getEventLog() {
		if(eventLog == null)	{
			eventLog = new EventLog();
		}
		return eventLog;
	}

	public SelectionDispatcher getSelectionDispatcher() {
		if(selectionDispatcher == null)	{
			selectionDispatcher = new SelectionDispatcher();
		}
		return selectionDispatcher;
	}

	public MarkerEditing getMarkerEditing() {
		if(markerEditing == null)	{
			markerEditing = new MarkerEditing();
		}
		return markerEditing;
	}

	public GlobalMapModel getGlobalMapModel() {
		if (globalMapModel == null) {
			globalMapModel = new GlobalMapModel();
		}
		return globalMapModel;
	}
}
