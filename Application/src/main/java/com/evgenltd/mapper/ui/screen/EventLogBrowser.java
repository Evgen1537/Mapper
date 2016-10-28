package com.evgenltd.mapper.ui.screen;

import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.component.eventlog.EventLog;
import com.evgenltd.mapper.ui.component.eventlog.Message;
import com.evgenltd.mapper.ui.component.eventlog.MessageListCell;
import javafx.beans.InvalidationListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import org.controlsfx.dialog.ExceptionDialog;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 18-06-2016 17:17
 */
public class EventLogBrowser extends AbstractScreen {

	@FXML private Button clearButton;
	@FXML private ListView<Message> eventList;

	private final EventLog eventLog = UIContext.get().getEventLog();

	public EventLogBrowser() {
		initUI();
		loadList();
		updateButtonState();
	}

	private void initUI()	{
		eventList.setCellFactory(param -> new MessageListCell());
		eventList.getItems().addListener((InvalidationListener)observable -> updateButtonState());
		eventList.setOnMouseReleased(this::handleEventListMouseReleased);
		eventLog.getMessageList().addListener((InvalidationListener)param -> loadList());
	}

	private void loadList()	{
		eventList.getItems().setAll(eventLog.getMessageList());
	}

	private void updateButtonState()	{
		clearButton.setDisable(eventList.getItems().isEmpty());
	}

	private void handleEventListMouseReleased(MouseEvent event)	{
		if(event.getClickCount() != 2)	{
			return;
		}

		final Message selectedMessage = eventList.getSelectionModel().getSelectedItem();
		if(selectedMessage == null)	{
			return;
		}
		if(!selectedMessage.getType().isError())	{
			return;
		}

		final ExceptionDialog dialog = new ExceptionDialog(selectedMessage.getException());
		dialog.showAndWait();
	}

	@FXML
	private void handleClear(ActionEvent event) {
		eventLog.clearLog();
	}
}
