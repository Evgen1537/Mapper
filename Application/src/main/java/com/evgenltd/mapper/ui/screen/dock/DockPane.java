package com.evgenltd.mapper.ui.screen.dock;

import com.evgenltd.mapper.ui.screen.AbstractScreen;
import com.evgenltd.mapper.ui.screen.IndicationProvider;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 13-06-2016 19:06
 */
public class DockPane extends BorderPane {

	private final SplitPane horizontalPane = new SplitPane();
	private final SplitPane verticalPane = new SplitPane();

	private DockSide left = new DockSide(DockSide.Position.LEFT, horizontalPane);
	private DockSide right = new DockSide(DockSide.Position.RIGHT, horizontalPane);
	private DockSide bottom = new DockSide(DockSide.Position.BOTTOM, verticalPane);

	public DockPane() {
		verticalPane.setOrientation(Orientation.VERTICAL);
		verticalPane.getItems().add(horizontalPane);
		setCenter(verticalPane);
		setLeft(left.getSidePane());
		setRight(right.getSidePane());
		setBottom(bottom.getSidePane());
	}

	public void refresh()	{
		left.refresh();
		right.refresh();
		bottom.refresh();
	}

	public void dockCenter(@NotNull final Node screen)	{
		horizontalPane.getItems().add(screen);
	}

	public void dockLeft(final Image icon, final String title, @NotNull final Supplier<AbstractScreen> screenBuilder) {
		left.addButton(icon, title, screenBuilder);
	}

	public void dockLeft(
			final Image icon,
			final String title,
			@NotNull final Supplier<AbstractScreen> screenBuilder,
			@NotNull final IndicationProvider indicationProvider
	) {
		left.addButton(icon, title, screenBuilder, indicationProvider);
	}

	public void dockRight(final Image icon, final String title, @NotNull final Supplier<AbstractScreen> screenBuilder) {
		right.addButton(icon, title, screenBuilder);
	}

	public void dockRight(
			final Image icon,
			final String title,
			@NotNull final Supplier<AbstractScreen> screenBuilder,
			@NotNull final IndicationProvider indicationProvider
	) {
		right.addButton(icon, title, screenBuilder, indicationProvider);
	}

	public void dockBottom(final Image icon, final String title, @NotNull final Supplier<AbstractScreen> screenBuilder) {
		bottom.addButton(icon, title, screenBuilder);
	}

	public void dockBottom(
			final Image icon,
			final String title,
			@NotNull final Supplier<AbstractScreen> screenBuilder,
			@NotNull final IndicationProvider indicationProvider
	) {
		bottom.addButton(icon, title, screenBuilder, indicationProvider);
	}
}
