package com.evgenltd.mapper.ui.screen;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.bean.ImporterBean;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.util.UIUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 08-07-2016 00:28
 */
public class Importer extends DialogScreen<Void> {

	private static final Logger log = LogManager.getLogger(Importer.class);

	private static final String IMPORTER_BACKGROUND_TASK = "IMPORTER_BACKGROUND_TASK";

	@FXML private CheckBox skipLayersImporting;
	@FXML private CheckBox skipMarkersImporting;
	@FXML private TextField appPath;
	@FXML private TextArea executionLog;
	@FXML private ProgressBar progress;

	private ButtonType doImportButtonType;
	private ButtonType cancelButtonType;
	private ButtonType closeButtonType;

	private Button doImport;
	private Button cancel;
	private Button close;

	private File applicationPath;

	private final ImporterBean importerBean = Context.get().getImporterBean();

	private Task<Void> backgroundImportTask;

	public Importer() {
		initUI();
	}

	private void initUI()	{

		executionLog.clear();
		executionLog.setEditable(false);

		importerBean.setMessageConsumer(message -> Platform.runLater(() -> appendMessage(message)));

		doImport = (Button)getDialog().getDialogPane().lookupButton(doImportButtonType);
		doImport.setDisable(true);
		doImport.addEventFilter(ActionEvent.ACTION, event -> {
			event.consume();
			startImport();
		});

		cancel = (Button)getDialog().getDialogPane().lookupButton(cancelButtonType);
		cancel.setDisable(true);
		cancel.addEventFilter(ActionEvent.ACTION, event -> {
			event.consume();
			cancelImport();
		});

		close = (Button)getDialog().getDialogPane().lookupButton(closeButtonType);
		close.setDisable(false);
	}

	@Override
	protected String getTitle() {
		return "Import from 1.x version";
	}

	@Override
	protected List<ButtonType> getButtonTypes() {
		return Arrays.asList(
				doImportButtonType = new ButtonType("Import"),
				cancelButtonType = new ButtonType("Cancel"),
				closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE)
		);
	}

	//

	private void startImport()	{
		doImport.setDisable(true);
		cancel.setDisable(false);
		close.setDisable(true);

		backgroundImportTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {

				importerBean.doImport(
						applicationPath,
						skipLayersImporting.isSelected(),
						skipMarkersImporting.isSelected(),
						this::updateProgress
				);
				return null;

			}
		};
		backgroundImportTask.setOnSucceeded(event -> taskCompleted());
		backgroundImportTask.setOnCancelled(event -> taskCancelled());
		backgroundImportTask.setOnFailed(event -> taskFailed());

		backgroundImportTask.progressProperty().addListener((observable, oldValue, newValue) -> {
			Platform.runLater(() -> progress.setProgress(newValue.doubleValue()));
		});

		UIContext.get().submit(IMPORTER_BACKGROUND_TASK, backgroundImportTask);
	}

	private void cancelImport()	{
		if(backgroundImportTask != null)	{
			backgroundImportTask.cancel();
		}
	}

	private void taskFinished()	{
		cancel.setDisable(true);
		close.setDisable(false);
		progress.progressProperty().unbind();
		progress.setProgress(0);
	}

	private void taskCompleted()	{
		taskFinished();
		appendMessage("Importing completed");
	}

	private void taskCancelled()	{
		taskFinished();
		appendMessage("Importing cancelled");
	}

	private void taskFailed()	{
		taskFinished();
		appendMessage("An error has occurred, see application log for more details");
		log.error("Importing failed",backgroundImportTask.getException());
	}

	// ui handlers

	private void appendMessage(final String message)	{
		executionLog.appendText(message+"\n");
	}

	@FXML
	private void handleBrowse(ActionEvent event) {

		UIUtils.askDirectory().ifPresent(appDirectory -> {

			applicationPath = appDirectory;
			appPath.setText(appDirectory.getAbsolutePath());
			final boolean checkResult = !importerBean.checkApplicationPath(applicationPath);
			doImport.setDisable(checkResult);

		});


	}
}
