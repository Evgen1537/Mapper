package com.evgenltd.mapper.ui.screen.dock;

import com.evgenltd.mapper.ui.screen.AbstractScreen;
import com.evgenltd.mapper.ui.screen.IndicationProvider;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 13-06-2016 22:47
 */
public class DockSide {
	private final ToggleGroup toggleGroup = new ToggleGroup();
	private Pane sidePane;
	private SplitPane contentPane;
	private double dividerPosition;
	private Position position;
	private final Map<Node,AbstractScreen> nodeToScreenMap = new HashMap<>();

	public DockSide(
			@NotNull final Position position,
			@NotNull final SplitPane contentPane
	) {
		this.position = position;
		this.sidePane = getPaneByPosition(position);
		this.contentPane = contentPane;
		this.dividerPosition = getDefaultDividerByPosition(position);
	}

	public Pane getSidePane() {
		return sidePane;
	}

	public void addButton(
			final Image icon,
			final String title,
			@NotNull final Supplier<AbstractScreen> screenBuilder
	)	{
		addButton(icon, title, screenBuilder, indicationHandler -> {});
	}

	public void addButton(
			final Image icon,
			final String title,
			@NotNull final Supplier<AbstractScreen> screenBuilder,
			@NotNull final IndicationProvider indicationProvider
	)	{

		final Label label = new Label();
		if(icon != null)	{
			label.setGraphic(new ImageView(icon));
		}
		if(title != null)	{
			label.setText(title);
		}

		final Pane indicator = new Pane();
		indicator.getStyleClass().add("side-bar-indicator");
		indicator.setVisible(false);

		final StackPane content = new StackPane();
		content.getChildren().add(label);
		content.getChildren().add(indicator);
		StackPane.setAlignment(indicator, Pos.TOP_RIGHT);

		final ToggleButton button = new ToggleButton();
		button.setGraphic(content);
		button.setToggleGroup(toggleGroup);
		button.setRotate(getRotationByPosition(position));
		button.selectedProperty().addListener(param -> handleToggleButtonAction(button,indicator,screenBuilder));
		button.getStyleClass().add("side-bar-button");

		indicationProvider.registerListener(() -> {
			if(!button.isSelected())	{
				indicator.setVisible(true);
			}
		});

		final Group group = new Group();
		group.getChildren().add(button);

		sidePane.getChildren().add(group);
	}

	public void refresh()	{
		nodeToScreenMap.values().forEach(AbstractScreen::refresh);
	}

	private void handleToggleButtonAction(
			@NotNull final ToggleButton toggleButton,
			@NotNull final Pane indicator,
			@NotNull final Supplier<AbstractScreen> screenBuilder
	)	{
		if(toggleButton.isSelected())	{
			indicator.setVisible(false);
			final AbstractScreen screen = screenBuilder.get();
			nodeToScreenMap.put(screen.getRoot(),screen);
			addPaneByPosition(screen.getRoot());
		}else {
			removePaneByPosition();
		}
	}

	private void addPaneByPosition(@NotNull final Node pane)	{
		switch(position)	{
			case LEFT:
				addFirstPane(contentPane, pane, dividerPosition);
				break;
			default:
				addLastPane(contentPane, pane, dividerPosition);
				break;
		}
	}

	private void removePaneByPosition()	{
		switch(position)	{
			case LEFT:
				dividerPosition = removeFirstPane(contentPane, nodeToScreenMap);
				break;
			default:
				dividerPosition = removeLastPane(contentPane, nodeToScreenMap);
				break;
		}
	}

	private static int getRotationByPosition(@NotNull final Position position)	{
		switch(position)	{
			case LEFT:
				return -90;
			case RIGHT:
				return 90;
			default:
				return 0;
		}
	}

	private static Pane getPaneByPosition(@NotNull final Position position)	{
		switch(position)	{
			case BOTTOM:
				return new HBox();
			default:
				return new VBox();
		}
	}

	private static double getDefaultDividerByPosition(@NotNull final Position position)	{
		switch(position)	{
			case BOTTOM:
			case RIGHT:
				return 0.8;
			default:
				return 0.2;
		}
	}

	private static void addFirstPane(
			@NotNull final SplitPane splitPane,
			@NotNull final Node pane,
			final double dividerPosition
	)	{

		final double[] newDividers = splitFirstDivider(splitPane, dividerPosition);

		SplitPane.setResizableWithParent(pane, false);
		splitPane.getItems().add(0,pane);

		applyDividers(splitPane, newDividers);

	}

	private static void addLastPane(
			@NotNull final SplitPane splitPane,
			@NotNull final Node pane,
			final double dividerPosition
	)	{

		final double[] newDividers = splitLastDivider(splitPane, dividerPosition);

		SplitPane.setResizableWithParent(pane, false);
		splitPane.getItems().add(pane);

		applyDividers(splitPane, newDividers);

	}

	private static double removeFirstPane(
			@NotNull final SplitPane splitPane,
			@NotNull final Map<Node,AbstractScreen> nodeToScreenMap
	)	{


		final double firstDividerPosition = splitPane.getDividerPositions()[0];
		final double[] newDividers = joinFirstDivider(splitPane);

		final Node node = splitPane.getItems().remove(0);
		final AbstractScreen screen = nodeToScreenMap.get(node);
		screen.dispose();

		applyDividers(splitPane, newDividers);

		return firstDividerPosition;

	}

	private static double removeLastPane(
			@NotNull final SplitPane splitPane,
			@NotNull final Map<Node,AbstractScreen> nodeToScreenMap
	)	{

		final double[] dividers = splitPane.getDividerPositions();
		final double lastDividerPosition = dividers[dividers.length-1];
		final List<Node> items = splitPane.getItems();
		final Node node = items.remove(items.size()-1);
		final AbstractScreen screen = nodeToScreenMap.get(node);
		screen.dispose();
		return lastDividerPosition;

	}

	private static double[] splitFirstDivider(
			@NotNull final SplitPane splitPane,
			final double dividerPosition
	)	{

		final double[] dividers = splitPane.getDividerPositions();
		final double[] result = new double[dividers.length + 1];

		result[0] = dividerPosition;
		System.arraycopy(dividers, 0, result, 1, dividers.length);

		return result;

	}

	private static double[] splitLastDivider(
			@NotNull final SplitPane splitPane,
			final double dividerPosition
	)	{

		final double[] dividers = splitPane.getDividerPositions();
		final int size = dividers.length;

		final double[] result = new double[size+1];
		System.arraycopy(dividers, 0, result, 0, dividers.length);
		result[size] = dividerPosition;

		return result;

	}

	private static double[] joinFirstDivider(@NotNull final SplitPane splitPane)	{

		final double[] dividers = splitPane.getDividerPositions();
		final double[] result = new double[dividers.length - 1];

		System.arraycopy(dividers, 1, result, 0, dividers.length - 1);

		return result;

	}

	private static void applyDividers(@NotNull final SplitPane splitPane, @NotNull final double[] dividers) {

		for(int i = 0; i < dividers.length; i++) {
			splitPane.setDividerPosition(i, dividers[i]);
		}

	}

	enum Position	{
		LEFT,RIGHT,BOTTOM
	}
}
