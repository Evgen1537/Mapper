package com.evgenltd.mapper.ui.component.command;

import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.component.eventlog.Message;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyCombination;
import org.controlsfx.control.action.Action;
import org.jetbrains.annotations.NotNull;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 18-06-2016 18:18
 */
public abstract class Command extends Action {

	private String id;
	private String longTextTemplate;
	private Class<?> scope;
	private String path;
	private int position;

	public Command() {
		super("");
		setEventHandler(this::execute);
	}

	public String getId() {
		return id;
	}

	void setId(String id) {
		this.id = id;
	}

	public String getLongTextTemplate() {
		return longTextTemplate;
	}

	void setLongTextTemplate(String longTextTemplate) {
		this.longTextTemplate = longTextTemplate;
	}

	public Class<?> getScope() {
		return scope;
	}

	void setScope(Class<?> scope) {
		this.scope = scope;
	}

	public String getPath() {
		return path;
	}

	void setPath(String path) {
		this.path = path;
	}

	public int getPosition() {
		return position;
	}

	void setPosition(int position) {
		this.position = position;
	}

	void updateLongText()	{
		final KeyCombination keyCombination = getAccelerator();
		if(keyCombination == null)	{
			setLongText(getLongTextTemplate());
		}else {
			setLongText(String.format(
					"%s (%s)",
					getLongTextTemplate(),
					keyCombination.getName()
			));
		}
	}

	protected abstract void execute(final ActionEvent event);


	//

	protected void setupDefaultListeners(@NotNull Task<?> task)	{
		task.setOnSucceeded(this::handleSucceeded);
		task.setOnCancelled(this::handleCancelled);
		task.setOnFailed(event -> handleFailed(task.getException()));
	}

	private void handleSucceeded(WorkerStateEvent event)	{
		Message
				.information()
				.title(getText())
				.text("Command execution completed")
				.publish();
		UIContext.get().refresh();
	}

	private void handleCancelled(WorkerStateEvent event)	{
		Message
				.warning()
				.title(getText())
				.text("Command execution cancelled")
				.publish();
		UIContext.get().refresh();
	}

	private void handleFailed(@NotNull final Throwable exception)	{
		Message
				.error()
				.title(getText())
				.text("Command execution failed")
				.withException(exception)
				.publish();
		UIContext.get().refresh();
	}
}
