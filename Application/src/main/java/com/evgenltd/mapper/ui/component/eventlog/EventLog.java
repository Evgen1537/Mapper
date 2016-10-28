package com.evgenltd.mapper.ui.component.eventlog;

import com.evgenltd.mapper.ui.screen.IndicationProvider;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 18-06-2016 16:38
 */
public class EventLog implements IndicationProvider {

	private static final Logger log = LogManager.getLogger(EventLog.class);

	private final ObservableList<Message> messageList = FXCollections.observableArrayList();
	private final ObservableList<Message> readonlyMessageList = FXCollections.unmodifiableObservableList(messageList);

	public void publish(@NotNull final Message message)	{

		if(message.getType().isError())	{
			log.error(message.getTitle(), message.getException());
			Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, message.getText(), ButtonType.CLOSE).show());
		}

		Platform.runLater(() -> {
			messageList.add(message);
			FXCollections.sort(messageList);
		});

	}

	public void clearLog()	{
		messageList.clear();
	}

	public ObservableList<Message> getMessageList() {
		return readonlyMessageList;
	}

	@Override
	public void registerListener(final Runnable indicationHandler) {
		readonlyMessageList.addListener((ListChangeListener<Message>) c -> indicationHandler.run());
	}
}
