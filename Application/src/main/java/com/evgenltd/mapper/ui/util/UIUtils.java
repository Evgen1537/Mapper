package com.evgenltd.mapper.ui.util;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.bean.SettingsBean;
import com.evgenltd.mapper.mapviewer.common.PaintContext;
import com.sun.javafx.tk.Toolkit;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 14-06-2016 20:01
 */
public class UIUtils {

	// instant dialogs

	public static Optional<File> askDirectory()   {

		final SettingsBean settingsBean = Context.get().getSettingsBean();

		final DirectoryChooser chooser = new DirectoryChooser();
		final File initialDirectory = settingsBean.getLastUsedFolder();
		if(initialDirectory != null && initialDirectory.exists() && initialDirectory.isDirectory()) {
			chooser.setInitialDirectory(initialDirectory);
		}

		return Optional
				.ofNullable(chooser.showDialog(null))
				.map(selectedFile -> {
					settingsBean.setLastUsedFolder(selectedFile);
					return selectedFile;
				});
	}

	public static Optional<File> askFile()	{

		final SettingsBean settingsBean = Context.get().getSettingsBean();

		final FileChooser chooser = new FileChooser();
		final File initialDirectory = settingsBean.getLastUsedFolder();
		if(initialDirectory != null && initialDirectory.exists() && initialDirectory.isDirectory()) {
			chooser.setInitialDirectory(initialDirectory);
		}

		return Optional
				.ofNullable(chooser.showOpenDialog(null))
				.map(selectedFile -> {
					settingsBean.setLastUsedFolder(selectedFile.getParentFile());
					return selectedFile;
				});

	}

	// task and service

	public static <T> Task<T> makeTask(@NotNull final Supplier<T> taskBody)	{
		return new Task<T>() {
			@Override
			protected T call() throws Exception {
				return taskBody.get();
			}
		};
	}

	public static Task<Void> makeVoidTask(@NotNull final Runnable taskBody)	{
		return makeTask(() -> {
			taskBody.run();
			return null;
		});
	}

	public static <T> Service<T> makeService(@NotNull final Supplier<T> taskBody)	{
		return new Service<T>() {
			@Override
			protected Task<T> createTask() {
				return makeTask(taskBody);
			}
		};
	}

	public static <T> ScheduledService<T> makeScheduledService(@NotNull final Supplier<T> taskBody)	{
		return new ScheduledService<T>() {
			@Override
			protected Task<T> createTask() {
				return makeTask(taskBody);
			}
		};
	}

	// drawing - custom objects

	private static double getTextWidth(@NotNull final String text, @NotNull final GraphicsContext graphicsContext)	{
		return Toolkit
				.getToolkit()
				.getFontLoader()
				.computeStringWidth(text, graphicsContext.getFont());
	}

	private static double getTextHeight(@NotNull final String text, @NotNull final GraphicsContext graphicsContext)	{
		return Toolkit
				.getToolkit()
				.getFontLoader()
				.getFontMetrics(graphicsContext.getFont())
				.getLineHeight();
	}

	// todo rewrite it
	public static void drawMarkerQuality(
			@NotNull final PaintContext context,
			final double worldX,
			final double worldY,
			String essence,
			String substance,
			String vitality
	) {
		essence = essence == null || essence.trim().isEmpty() ? "0" : essence;
		substance = substance == null || substance.trim().isEmpty() ? "0" : substance;
		vitality = vitality == null || vitality.trim().isEmpty() ? "0" : vitality;

		essence = essence.length() > 5 ? essence.substring(0,5) + "..." : essence;
		substance = substance.length() > 5 ? substance.substring(0,5) + "..." : substance;
		vitality = vitality.length() > 5 ? vitality.substring(0,5) + "..." : vitality;

		final GraphicsContext graphicsContext = context.getGraphicsContext();

		final double spaceWidth = getTextWidth(" ", graphicsContext);
		final double spaceHeight = getTextHeight(" ", graphicsContext);
		final double essenceWidth = getTextWidth(essence, graphicsContext);
		final double substanceWidth = getTextWidth(substance, graphicsContext);
		final double vitalityWidth = getTextWidth(vitality, graphicsContext);
		final double totalWidth = essenceWidth + spaceWidth + substanceWidth + spaceWidth + vitalityWidth;
		final double canvasX = context.toCanvasX(worldX);
		final double canvasY = context.toCanvasY(worldY)
				- (UIConstants.MARKER_SIZE / context.getLevel().getMeasure()) / 2
				- spaceHeight;
		final double gap = 2;
		final double roundRadius = 6;

		graphicsContext.setFill(Color.WHITE);
		graphicsContext.fillRoundRect(
				canvasX - totalWidth / 2 - gap,
				canvasY - spaceHeight / 2 - gap,
				totalWidth + 2 * gap,
				spaceHeight + 2 * gap,
				roundRadius,
				roundRadius
		);
		graphicsContext.setStroke(Color.BLACK);
		graphicsContext.strokeRoundRect(
				canvasX - totalWidth / 2 - gap,
				canvasY - spaceHeight / 2 - gap,
				totalWidth + 2 * gap,
				spaceHeight + 2 * gap,
				roundRadius,
				roundRadius
		);

		graphicsContext.setTextBaseline(VPos.CENTER);

		final double essenceX = canvasX - totalWidth / 2;
		graphicsContext.setFill(UIConstants.ESSENCE_COLOR);
		graphicsContext.fillText(essence, canvasX - totalWidth / 2, canvasY);

		final double substanceX = essenceX + essenceWidth + spaceWidth;
		graphicsContext.setFill(UIConstants.SUBSTANCE_COLOR);
		graphicsContext.fillText(substance, substanceX, canvasY);

		final double vitalityX = substanceX + substanceWidth + spaceWidth;
		graphicsContext.setFill(UIConstants.VITALITY_COLOR);
		graphicsContext.fillText(vitality, vitalityX, canvasY);

		graphicsContext.setTextBaseline(VPos.BASELINE);
	}

	// other

	public static Image fitMarkerTypeImage(File source) throws IOException {
		final BufferedImage image = ImageIO.read(source);

		int width = image.getWidth();
		int height = image.getHeight();

		if(width > UIConstants.MARKER_SIZE || height > UIConstants.MARKER_SIZE) {   // should be scaled
			final double scale = (double)(width > height ? width : height) / UIConstants.MARKER_SIZE;
			width = (int)(width / scale);
			height = (int)(height / scale);
		}

		final int x = width < UIConstants.MARKER_SIZE ? (UIConstants.MARKER_SIZE - width) / 2 : 0;
		final int y = height < UIConstants.MARKER_SIZE ? (UIConstants.MARKER_SIZE - height) / 2 : 0;

		final BufferedImage result = new BufferedImage(UIConstants.MARKER_SIZE, UIConstants.MARKER_SIZE, 2);
		result.getGraphics().drawImage(image, x, y, width, height, null);

		return SwingFXUtils.toFXImage(result, null);
	}

	public static <T> Predicate<T> firstOccurrencePredicate(@NotNull final Predicate<T> predicate)	{
		return new Predicate<T>() {

			private boolean firstConditionSucceed = false;

			@Override
			public boolean test(T t) {

				if(!firstConditionSucceed & predicate.test(t))	{
					firstConditionSucceed = true;
					return true;
				}else {
					return false;
				}

			}
		};
	}
}
