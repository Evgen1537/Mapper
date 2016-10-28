package com.evgenltd.mapper.mapviewer.common;

import com.evgenltd.mapper.mapviewer.util.Utils;
import javafx.beans.Observable;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Slider;
import javafx.scene.input.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import math.geom2d.Point2D;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Project: MapperPrototype
 * Author:  Evgeniy
 * Created: 08-06-2016 22:49
 */
public class MapViewer extends StackPane {

	private static final Color SELECTION_RECT_COLOR = Color.LIGHTGREEN;

	private ResizeableCanvasHolder canvasHolder;
	private final Canvas canvas = new Canvas();
	private Slider slider;

	private State state = State.NONE;
	private final NodeContainer nodeContainer = new NodeContainer();
	private final EventProperties eventProperties = new EventProperties();
	private final Viewport viewport = new Viewport();
	private final SelectionRect selectionRect = new SelectionRect();
	private boolean rectSelectionAllowed = true;
	private final NavigationPoint navigationPoint = new NavigationPoint();
	private final NavigationPoint movePoint = new NavigationPoint();
	private Function<Point2D,Boolean> pointTargetFunction;
	private ContextMenu contextMenu;

	private double cursorWorldX;
	private double cursorWorldY;
	private boolean editingEnabled;

	private final GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
	private final PaintContext paintContext = new PaintContext(graphicsContext, viewport);

	public MapViewer() {
		initUI();
	}

	private void initUI()	{

		canvas.widthProperty().addListener(this::handleBoundsChanged);
		canvas.heightProperty().addListener(this::handleBoundsChanged);
		canvas.setOnMousePressed(this::handleMousePressed);
		canvas.setOnMouseDragged(this::handleMouseDragged);
		canvas.setOnMouseReleased(this::handleMouseReleased);
		canvas.setOnMouseMoved(this::handleMouseMoved);
		canvas.setOnScroll(this::handleCanvasScrollEvent);
		setOnKeyReleased(this::handleKeyReleased);

		canvasHolder = new ResizeableCanvasHolder();
		canvasHolder.setCanvas(canvas);
		canvasHolder.setPrefSize(400,300);

		slider = new Slider();
		slider.setOrientation(Orientation.VERTICAL);
		slider.setMajorTickUnit(1.0);
		slider.setMinorTickCount(0);
		slider.setMax(ZLevel.MAX_LEVEL.getLevel());
		slider.setMin(ZLevel.MIN_LEVEL.getLevel());
		slider.setShowTickLabels(true);
		slider.setShowTickMarks(true);
		slider.setSnapToTicks(true);
		slider.setMaxHeight(200);
		StackPane.setAlignment(slider, Pos.TOP_RIGHT);

		getChildren().addAll(canvasHolder,slider);

		viewport.levelProperty().bindBidirectional(slider.valueProperty());
		viewport.levelProperty().addListener(this::handleBoundsChanged);

	}

	// ############################################################
	// #                                                          #
	// #                         API                              #
	// #                                                          #
	// ############################################################

	// updating

	/**
	 * Performs merging component nodes with specified node list.
	 * @param nodeList source node list which will be merged with component's nodes
	 */
	public void mergeNodes(@NotNull final List<Node> nodeList) {
		nodeContainer.mergeNodes(nodeList);
		keepNodesBlocked();
		refresh();
	}

	private void clearNodeState()	{
		editingEnabled = false;
		nodeContainer.getNodes().forEach(node -> {
			node.setBlocked(false);
			node.setSelected(n -> false);
			node.setHighlighted(n -> false);
			node.setEditing(false);
			node.setMoved(n -> false);
		});
		eventProperties.fireHighlightChanged();
		eventProperties.fireSelectionChanged();
	}

	// highlight

	/**
	 *
	 */
	public void clearHighlight() {
		updateHighlight(node -> false);
	}

	/**
	 *
	 * @param condition
	 */
	public void updateHighlight(@NotNull Predicate<Node> condition)	{
		if(nodeContainer.updateHighlight(condition))	{
			eventProperties.fireHighlightChanged();
		}
		refresh();
	}

	// selection

	/**
	 *
	 */
	public void clearSelection() {
		updateSelection(node -> false);
	}

	/**
	 *
	 * @param condition
	 */
	public void updateSelection(@NotNull Predicate<Node> condition) {
		if(nodeContainer.updateSelection(condition))	{
			eventProperties.fireSelectionChanged();
		}
		refresh();
	}

	/**
	 *
	 * @param selectedNodeFilter
	 * @return
	 */
	public List<Node> getSelectedNodes(@NotNull final Predicate<Node> selectedNodeFilter) {
		return nodeContainer.getSelectedNodes(selectedNodeFilter);
	}

	/**
	 *
	 * @param selectedNodeFilter
	 * @return
	 */
	public boolean isNodeSelected(@NotNull final Predicate<Node> selectedNodeFilter) {
		return nodeContainer.isNodeSelected(selectedNodeFilter);
	}

	// rect restriction

	/**
	 *
	 * @return
	 */
	public boolean isRectSelectionAllowed() {
		return rectSelectionAllowed;
	}

	/**
	 *
	 * @param rectSelectionAllowed
	 */
	public void setRectSelectionAllowed(boolean rectSelectionAllowed) {
		this.rectSelectionAllowed = rectSelectionAllowed;
	}

	// viewport properties

	public double getViewportX() {
		return viewport.getX();
	}

	public double getViewportY() {
		return viewport.getY();
	}

	public double getViewportCentroidX()	{
		return viewport.getX() + viewport.getWidth() / 2;
	}

	public double getViewportCentroidY()	{
		return viewport.getY() + viewport.getHeight() / 2;
	}

	public double getViewportWidth() {
		return viewport.getWidth();
	}

	public double getViewportHeight() {
		return viewport.getHeight();
	}

	public ZLevel getViewportLevel() {
		return viewport.getLevel();
	}

	public double getCursorX() {
		return cursorWorldX;
	}

	public double getCursorY() {
		return cursorWorldY;
	}

	// point selection

	public void beginTargetingPoint(@NotNull final Function<Point2D,Boolean> callback)	{
		if(!state.isNone())	{
			throw new IllegalStateException(String.format("Unable to target point in %s state",state));
		}
		pointTargetFunction = callback;
		state = State.POINT_TARGET;
		setCursor(Cursor.CROSSHAIR);
	}

	public boolean isTargetingPoint()	{
		return state.isPointTarget();
	}

	// editing

	/**
	 * - all selected, highlighted, moved, blocked and state cleared
	 * - all nodes locked (exclude selected)
	 * - selected node become EDITED
	 */
	public void beginEditingNode(@NotNull final Node nodeForEditing)	{
		if(!nodeContainer.getNodes().contains(nodeForEditing))   {
			nodeContainer.addNode(nodeForEditing);
		}
		clearNodeState();
		editingEnabled = true;
		nodeForEditing.setEditing(true);
		nodeContainer.lockNodes(otherNode -> !Objects.equals(otherNode,nodeForEditing));
		refresh();
	}

	public void endEditingNode()	{
		clearNodeState();
		refresh();
	}

	void keepNodesBlocked()	{
		if(editingEnabled)	{
			nodeContainer.getNodes().forEach(node -> node.setBlocked(!node.isEditing()));
		}
	}

	// context menu

	public void setContextMenu(ContextMenu contextMenu) {
		this.contextMenu = contextMenu;
		setOnContextMenuRequested(event -> contextMenu.show(this, event.getScreenX(), event.getScreenY()));
	}

	// move viewport to point

	public void moveToPoint(final double worldX, final double worldY)	{
		viewport.setX(worldX - viewport.getWidth() / 2);
		viewport.setY(worldY - viewport.getHeight() / 2);
		eventProperties.fireViewportChanged();
	}

	// events

	public void setOnHighlightChanged(EventHandler<MapViewerEvent> onHighlightChanged) {
		eventProperties.setOnHighlightChanged(onHighlightChanged);
	}

	public void setOnSelectionChanged(EventHandler<MapViewerEvent> onSelectionChanged) {
		eventProperties.setOnSelectionChanged(onSelectionChanged);
	}

	public void setOnMoved(EventHandler<MoveEvent> onMoved) {
		eventProperties.setOnMoved(onMoved);
	}

	public void setOnViewportChanged(EventHandler<MapViewerEvent> onViewportChanged) {
		eventProperties.setOnViewportChanged(onViewportChanged);
	}

	// ############################################################
	// #                                                          #
	// #                       Painting                           #
	// #                                                          #
	// ############################################################

	public void refresh()	{
		clearScreen();
		paintNodes();
		paintSelectionRect();
	}

	private void clearScreen()		{
		graphicsContext.setFill(Color.WHITE);
		graphicsContext.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
	}

	private void paintNodes()	{
		nodeContainer.getNodes().forEach(node -> node.paint(paintContext));
	}

	private void paintSelectionRect()	{
		if(!state.isSelection())	{
			return;
		}
		graphicsContext.setGlobalAlpha(0.3);
		graphicsContext.setFill(SELECTION_RECT_COLOR);
		graphicsContext.fillRect(
				selectionRect.getFirstX(),
				selectionRect.getFirstY(),
				selectionRect.getWidth(),
				selectionRect.getHeight()
		);
		graphicsContext.setGlobalAlpha(1.0);
	}

	// ############################################################
	// #                                                          #
	// #                  Workflow handlers                       #
	// #                                                          #
	// ############################################################

	private void handleCanvasScrollEvent(ScrollEvent event) {
		final Point3D p = event.getPickResult().getIntersectedPoint();
		final double previousWorldCursorX = viewport.toWorldX(p.getX());
		final double previousWorldCursorY = viewport.toWorldY(p.getY());

		if(event.getDeltaY() > 0) {
			// zoom in
			if(viewport.getLevel().isMinimumLevel())	{
				return;
			}
			viewport.decrementLevel();
		}else {
			// zoom out
			if(viewport.getLevel().isMaximumLevel())	{
				return;
			}
			viewport.incrementLevel();
		}

		final double newWorldScaledCursorX = viewport.toWorldSize(p.getX());
		final double newWorldScaledCursorY = viewport.toWorldSize(p.getY());

		final double newViewportPositionX = previousWorldCursorX - newWorldScaledCursorX;
		final double newViewportPositionY = previousWorldCursorY - newWorldScaledCursorY;

		viewport.setX(newViewportPositionX);
		viewport.setY(newViewportPositionY);

		eventProperties.fireViewportChanged();
		refresh();
	}

	private void handleBoundsChanged(Observable observable)	{
		boolean invalidated = viewport.setSizeFromCanvas(
				canvasHolder.getWidth(),
				canvasHolder.getHeight()
		);

		if(invalidated)	{
			eventProperties.fireViewportChanged();
		}
		refresh();
	}

	private void handleMousePressed(MouseEvent event)	{

		if(contextMenu != null)	{
			contextMenu.hide();
		}

		if(!state.isNone())	{
			return;
		}

		final double canvasX = event.getX();
		final double canvasY = event.getY();
		final MouseButton selectedButton = event.getButton();

		if(Objects.equals(selectedButton, MouseButton.PRIMARY))	{

			startSelection(canvasX,canvasY);

		}else if(Objects.equals(selectedButton, MouseButton.MIDDLE))	{

			startNavigation(canvasX,canvasY);

		}

	}

	private void handleMouseDragged(MouseEvent event)   {

		final double canvasX = event.getX();
		final double canvasY = event.getY();

		if(state.isClick()) {

			if (prepareNodesForMoving()) {

				startMoving();

			}else if(isRectSelectionAllowed()) {

				startRectSelection();
				increaseRectSelection(canvasX,canvasY);

			}

		}else if(state.isMove()) {

			moveNodes(canvasX,canvasY);

		}else if(state.isSelection()) {

			increaseRectSelection(canvasX,canvasY);

		}else if(state.isNavigation())    {

			performNavigation(canvasX,canvasY);

		}

	}

	private void handleMouseReleased(MouseEvent event)  {

		final MouseButton selectedButton = event.getButton();
		final boolean isControlDown = event.isControlDown();
		final boolean isShiftDown = event.isShiftDown();

		if(Objects.equals(selectedButton, MouseButton.PRIMARY)) {

			if(state.isAnySelection())	{

				completeSelection(isControlDown, state.isClick());
				clearControlState();

			}else if(state.isMove())    {

				completeMove();
				clearControlState();

			}else if(state.isPointTarget())	{

				completePointTarget(isShiftDown);

			}

		}else if(Objects.equals(selectedButton, MouseButton.MIDDLE)) {

			if(state.isNavigation())	{

				completeNavigation();

			}

			clearControlState();

		}

	}

	private void handleMouseMoved(MouseEvent event) {
		cursorWorldX = viewport.toWorldX(event.getX());
		cursorWorldY = viewport.toWorldY(event.getY());
		if(state.isPointTarget())	{
			refresh();
			return;
		}
		updateHighlight(Utils.firstOccurrencePredicate(node -> node.intersect(cursorWorldX, cursorWorldY)));
	}

	private void handleKeyReleased(KeyEvent keyEvent)	{
		if(Objects.equals(keyEvent.getCode(), KeyCode.ESCAPE))	{
			clearSelection();
			clearControlState();
		}
	}

	// ############################################################
	// #                                                          #
	// #                  Workflow methods                        #
	// #                                                          #
	// ############################################################

	private void clearControlState()	{
		selectionRect.setInitPosition(0,0);
		navigationPoint.setInitPosition(0,0);
		movePoint.setInitPosition(0,0);
		state = State.NONE;
		setCursor(Cursor.DEFAULT);
		refresh();
	}

	// selection

	private void startSelection(final double canvasX, final double canvasY)	{
		selectionRect.setInitPosition(canvasX,canvasY);
		state = State.CLICK;
		refresh();
	}

	private void startRectSelection()	{
		state = State.SELECTION;
	}

	private void increaseRectSelection(final double canvasX, final double canvasY)	{
		selectionRect.updateSecondPosition(canvasX, canvasY);
		refresh();
	}

	private void completeSelection(final boolean isAddSelection, final boolean isSingleSelection) {
		final double firstX = viewport.toWorldX(selectionRect.getFirstX());
		final double firstY = viewport.toWorldY(selectionRect.getFirstY());
		final double width = viewport.toWorldSize(selectionRect.getWidth());
		final double height = viewport.toWorldSize(selectionRect.getHeight());

		// select node if matches with conditions
		// - if keep selection and node already selected
		// - if node intersected with rect

//		final Predicate<Node> addSelectionCondition = node -> isAddSelection;
		final Predicate<Node> intersectCondition = isSingleSelection
				? Utils.firstOccurrencePredicate(node -> node.intersect(firstX, firstY, width, height))
				: node -> node.intersect(firstX, firstY, width, height);

		updateSelection(intersectCondition);

	}

	// navigation

	private void startNavigation(final double canvasX, final double canvasY)	{
		navigationPoint.setInitPosition(canvasX,canvasY);
		state = State.NAVIGATION;
		refresh();
	}

	private void performNavigation(final double canvasX, final double canvasY){
		navigationPoint.updatePosition(canvasX,canvasY);
		final boolean changed = viewport.changePositionByNavigationPoint(
				navigationPoint.getDeltaX(),
				navigationPoint.getDeltaY()
		);
		if(changed)	{
			eventProperties.fireViewportChanged();
		}
		refresh();
	}

	private void completeNavigation()	{
		eventProperties.fireViewportChanged();
		refresh();
	}

	// moving

	private boolean prepareNodesForMoving()	{
		return nodeContainer.updateMoved(Node::isSelected);
	}

	private void startMoving()	{
		state = State.MOVE;
		movePoint.setInitPosition(
				viewport.toWorldSize(selectionRect.getCanvasX()),
				viewport.toWorldSize(selectionRect.getCanvasY())
		);
		refresh();
	}

	private void moveNodes(final double canvasX, final double canvasY)	{
		movePoint.updatePosition(
				viewport.toWorldSize(canvasX),
				viewport.toWorldSize(canvasY)
		);
		nodeContainer.moveNodes(
				movePoint.getDeltaX(),
				movePoint.getDeltaY()
		);
		refresh();
	}

	private void completeMove()	{
		nodeContainer.clearMovingNodes();
		if(editingEnabled)	{
			return;
		}
		eventProperties.fireMoved(movePoint.getTotalDeltaX(), movePoint.getTotalDeltaY());
	}

	// targeting

	private void completePointTarget(boolean isShiftDown)	{
		final boolean isReplay = pointTargetFunction.apply(new Point2D(cursorWorldX, cursorWorldY));
		clearControlState();
		if(isReplay && isShiftDown)	{
			beginTargetingPoint(pointTargetFunction);
		}
	}

	// move step

	public void setMoveStep(final double step) {
		movePoint.setStep(step);
	}
}
