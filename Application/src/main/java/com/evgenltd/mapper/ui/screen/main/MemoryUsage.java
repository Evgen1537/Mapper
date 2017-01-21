package com.evgenltd.mapper.ui.screen.main;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Project: mapper
 * Author:  Evgeniy Lebedev
 * Created: 19-01-2017 00:30
 */
public class MemoryUsage extends StackPane {

	private static final long TIMEOUT = 1000;
	private static final Logger log = LogManager.getLogger(MemoryUsage.class);

	private ProgressBar memoryBar;
	private Label memoryLabel;

	public MemoryUsage() {
		memoryBar = new ProgressBar();
		memoryBar.setPrefWidth(150);
		memoryLabel = new Label();
		getChildren().addAll(memoryBar, memoryLabel);

		final Thread memoryUsageUpdater = new Thread(this::memoryUsageUpdaterBody);
		memoryUsageUpdater.setName("MemoryUsageUpdater");
		memoryUsageUpdater.setDaemon(true);
		memoryUsageUpdater.start();
	}

	private void memoryUsageUpdaterBody() {

		while (true) {

			try {

				final Runtime runtime = Runtime.getRuntime();
				final long freeMemory = runtime.freeMemory() / 1_000_000;
				final long totalMemory = runtime.totalMemory() / 1_000_000;
				final long usedMemory = totalMemory - freeMemory;

				Platform.runLater(() -> {
					memoryLabel.setText(String.format("%s MB / %s MB", usedMemory, totalMemory));
					memoryBar.setProgress((double) usedMemory / (double) totalMemory);
				});

				Thread.sleep(TIMEOUT);

			} catch(InterruptedException e) {
				return;
			} catch(Throwable e) {
				log.warn("Some error has occured in memory usage component", e);
			}

		}

	}
}
