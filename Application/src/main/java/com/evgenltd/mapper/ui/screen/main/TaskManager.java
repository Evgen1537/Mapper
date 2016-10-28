package com.evgenltd.mapper.ui.screen.main;

import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.concurrent.Task;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.PopupControl;
import javafx.scene.image.ImageView;
import javafx.stage.PopupWindow;
import org.controlsfx.control.StatusBar;
import org.controlsfx.control.TaskProgressView;

import java.util.Collection;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 08-07-2016 10:03
 */
public class TaskManager {

	private StatusBar statusBar;
	private final TaskProgressView<Task<?>> taskTaskProgressView = new TaskProgressView<>();
	private final PopupControl taskViewerHolder = new PopupControl();

	public TaskManager(StatusBar statusBar) {
		this.statusBar = statusBar;
		initTaskViewer();
		initPopupControl();
		initShowTasksButton();
	}

	private void initTaskViewer()	{
		taskTaskProgressView.setMaxSize(300,300);
		UIContext
				.get()
				.addTaskListChangedListener(this::updateStatusBar);
	}

	private void initPopupControl()	{
		taskViewerHolder.setAutoHide(true);
		taskViewerHolder.getScene().setRoot(taskTaskProgressView);
		taskViewerHolder.setAnchorLocation(PopupWindow.AnchorLocation.CONTENT_BOTTOM_RIGHT);
	}

	private void initShowTasksButton()	{
		final Button button = new Button("", new ImageView(UIConstants.CLIPBOARD_TASK));
		button.setOnAction(event -> showHideTaskViewer());
		button.getStyleClass().add("status-bar-button");

		statusBar.setText("");
		statusBar.getRightItems().addAll(button);
	}

	private void updateStatusBar()	{
		final Collection<Task<?>> taskList = UIContext.get().getTaskList().values();
		taskTaskProgressView.getTasks().setAll(taskList);

		if(taskList.isEmpty()) {
			statusBar.progressProperty().unbind();
			statusBar.textProperty().unbind();
			statusBar.setProgress(0);
			statusBar.setText("");
		}else if(taskList.size() == 1) {
			final Task<?> task = taskList.iterator().next();
			statusBar.progressProperty().bind(task.progressProperty());
			statusBar.textProperty().bind(task.titleProperty());
		}else {
			statusBar.progressProperty().unbind();
			statusBar.textProperty().unbind();
			statusBar.setProgress(-1);
			statusBar.setText("Background tasks in progress...");
		}
	}

	private void showHideTaskViewer()	{

		final Bounds bounds = statusBar.localToScreen(statusBar.getBoundsInLocal());
		final double x = bounds.getMaxX();
		final double y = bounds.getMinY();
		taskViewerHolder.show(UIContext.get().getStage(), x, y);

	}
}
