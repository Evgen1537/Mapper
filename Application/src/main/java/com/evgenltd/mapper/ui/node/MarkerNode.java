package com.evgenltd.mapper.ui.node;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.bean.SettingsBean;
import com.evgenltd.mapper.core.entity.Marker;
import com.evgenltd.mapper.core.entity.MarkerPoint;
import com.evgenltd.mapper.core.rule.MarkerPointSuggestions;
import com.evgenltd.mapper.core.rule.Reordering;
import com.evgenltd.mapper.core.util.Constants;
import com.evgenltd.mapper.mapviewer.common.Node;
import com.evgenltd.mapper.mapviewer.common.NodeGroup;
import com.evgenltd.mapper.mapviewer.common.PaintContext;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.util.Geometry;
import com.evgenltd.mapper.ui.util.UIConstants;
import com.evgenltd.mapper.ui.util.UIUtils;
import javafx.scene.paint.Color;
import math.geom2d.Point2D;
import math.geom2d.polygon.Polygon2D;
import math.geom2d.polygon.Polyline2D;
import math.geom2d.polygon.Rectangle2D;
import math.geom2d.polygon.SimplePolygon2D;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 17-06-2016 02:33
 */
public class MarkerNode extends NodeGroup {

	private Marker marker;
	private long newMarkerIdentity;

	private Point2D point;
	private Polygon2D polygon;
	private Polygon2D polygonForIntersection;
	private Polyline2D polyline;

	// todo reimplement
	private Optional<MarkerPoint> firstSuggestedPoint = Optional.empty();
	private Optional<MarkerPoint> secondSuggestedPoint = Optional.empty();

	private final SettingsBean settingsBean = Context.get().getSettingsBean();

	public MarkerNode(Marker marker) {
		this.marker = marker;
		if(marker.getId() == null)	{
			newMarkerIdentity = UIContext.get().getMarkerEditing().getNextMarkerIdentity();
		}
		setOrderNumber(Constants.MARKER_ORDER_POSITION);
	}

	private long getIndexForInsertPoint()	{
		final Optional<Long> firstIndexHolder = firstSuggestedPoint.map(MarkerPoint::getOrderNumber);
		final Optional<Long> secondIndexHolder = secondSuggestedPoint.map(MarkerPoint::getOrderNumber);

		// for track markers
		if(!secondIndexHolder.isPresent())	{
			return firstIndexHolder.get() == 1 ? 1 : firstIndexHolder.get() + 1;
		}

		final long firstIndex = firstIndexHolder.get();
		final long secondIndex = secondIndexHolder.get();
		// in case when first and second indexes placed at the border of the collection
		return Math.abs(firstIndex - secondIndex) > 1
				? Long.min(firstIndex, secondIndex)
				: Long.max(firstIndex, secondIndex);
	}

	public Marker getMarker() {
		return marker;
	}

	//

	private void prepareNodeChildren()	{
		clear();
		final List<MarkerPoint> pointListCopy = new ArrayList<>(marker.getMarkerPointList());
		pointListCopy.forEach(markerPoint -> {
			final MarkerPointNode markerPointNode = new MarkerPointNode(this,markerPoint);
			super.add(markerPointNode);
		});
	}

	private void prepareMarkerPoints()	{
		final Collection<MarkerPoint> markerPointList = marker.getMarkerPointList();

		point = null;
		polyline = null;
		polygon = null;
		polygonForIntersection = null;

		if(markerPointList.size() == 1)	{
			prepareBySingularPoint(new ArrayList<>(markerPointList));
		}else {
			prepareByComplexShape(new ArrayList<>(markerPointList));
		}
	}

	private void prepareBySingularPoint(@NotNull final List<MarkerPoint> markerPointList)	{
		final MarkerPoint markerPointNode = markerPointList.get(0);
		polygon = Geometry.getRectangle(
				markerPointNode.getX(),
				markerPointNode.getY(),
				UIConstants.MARKER_SIZE,
				UIConstants.MARKER_SIZE
		);
		polygonForIntersection = polygon;
		point = new Point2D(markerPointNode.getX(), markerPointNode.getY());
	}

	@SuppressWarnings("unchecked")
	private void prepareByComplexShape(@NotNull final List<MarkerPoint> markerPointList)	{
		final Collection<Point2D> vertexCollection = markerPointList
				.stream()
				.sorted(MarkerPoint.MARKER_POINT_COMPARATOR)
				.map(markerPoint -> new Point2D(markerPoint.getX(), markerPoint.getY()))
				.collect(Collectors.toList());

		if(marker.getType().isTrack())	{
			polyline = new Polyline2D(vertexCollection);
		}else {

			polygon = new SimplePolygon2D(vertexCollection);
			point = new SimplePolygon2D(vertexCollection).centroid();
			final List<Point2D> clockwiseSorted = new ArrayList<>(vertexCollection);
			clockwiseSorted.sort(getClockwiseComparator(point));
			polygonForIntersection = new SimplePolygon2D(clockwiseSorted);
		}
	}

	//

	@Override
	public void remove(@NotNull final Node node) {
		super.remove(node);
		final MarkerPointNode markerPointNode = (MarkerPointNode)node;

		Reordering.removeOrdered(marker.getMarkerPointList(), markerPointNode.getMarkerPoint());
	}

	@Override
	public void add(@NotNull final Node node) {
		super.add(node);

		final MarkerPointNode markerPointNode = (MarkerPointNode)node;
		final MarkerPoint markerPoint = markerPointNode.getMarkerPoint();

		marker.getMarkerPointList().add(markerPoint);
	}

	public void insert(@NotNull final Node node)	{
		super.add(node);

		final MarkerPointNode markerPointNode = (MarkerPointNode)node;
		final MarkerPoint markerPoint = markerPointNode.getMarkerPoint();

		markerPoint.setOrderNumber(getIndexForInsertPoint());
		Reordering.insertOrdered(marker.getMarkerPointList(), markerPoint);
	}

	@Override
	protected void setEditing(boolean editing) {
		super.setEditing(editing);
		prepareNodeChildren();
	}

	@Override
	public long getIdentifier() {
		return marker.getId() == null
				? newMarkerIdentity
				: marker.getId();
	}

	@Override
	public boolean intersect(double worldX, double worldY) {

		if(!isAccessible()) {
			return false;
		}

		if(isEditing()) {
			return super.intersect(worldX, worldY);
		}

		return intersect(
				worldX - UIConstants.SELECTION_CLICK_GAP,
				worldY - UIConstants.SELECTION_CLICK_GAP,
				2 * UIConstants.SELECTION_CLICK_GAP,
				2 * UIConstants.SELECTION_CLICK_GAP
		);

	}

	@Override
	public boolean intersect(double cornerWorldX, double cornerWorldY, double worldWidth, double worldHeight) {

		if(!isAccessible()) {
			return false;
		}

		if(isEditing())	{
			return super.intersect(cornerWorldX,cornerWorldY,worldWidth,worldHeight);
		}

		final Rectangle2D selectionRect = new Rectangle2D(cornerWorldX, cornerWorldY, worldWidth, worldHeight);

		if(polygonForIntersection != null)	{

			return Geometry.intersect(selectionRect, polygonForIntersection);

		}else if(polyline != null)	{

			return Geometry.intersect(selectionRect, polyline);

		}

		return false;

	}

	@Override
	public void move(double worldDeltaX, double worldDeltaY) {
		if(isEditing()) {
			super.move(worldDeltaX, worldDeltaY);
		}else {
			marker.getMarkerPointList().forEach(markerPoint -> {
				markerPoint.setX(markerPoint.getX() + worldDeltaX);
				markerPoint.setY(markerPoint.getY() + worldDeltaY);
			});
		}
	}

	// paint

	@Override
	public void paint(PaintContext context) {

		if(!isMarkerVisible()) {
			return;
		}

		if(!marker.getMarkerPointList().isEmpty()) {

			prepareMarkerPoints();

			drawMarkerOutline(context);
			drawSuggestedOutline(context, firstSuggestedPoint);
			drawSuggestedOutline(context, secondSuggestedPoint);
			if(isEditing())	super.paint(context);
			drawPoint(context, getCursorX(), getCursorY());

		}

		drawMarkerImage(context);

		drawMarkerQuality(context);

		if(!isEditing() || !isTargetingPoint()) {
			return;
		}

		calculateSuggestedPoints();

	}

	private void drawMarkerOutline(PaintContext context) {

		// @formatter:off
		final Color color =
						isHighlighted() ? UIConstants.HIGHLIGHT_COLOR
						: isSelected() 	? UIConstants.SELECTION_COLOR
						: UIConstants.DEFAULT_MARKER_COLOR;
		// @formatter:on

		final Collection<Point2D> vertices =
						polygon != null ? polygon.vertices()
						: polyline != null ? polyline.vertices()
						: Collections.emptyList();

		final double[] xPoints = getPoints(vertices, Point2D::x);
		final double[] yPoints = getPoints(vertices, Point2D::y);
		final int nPoint = vertices.size();
		final double lineWidth = (
				marker.getType().isTrack()
						? UIConstants.TRACK_WIDTH
						: UIConstants.AREA_OUTLINE_BORDER_WIDTH
		) + (
				isHighlighted() 	? UIConstants.HIGHLIGHT_OUTLINE_WIDTH :
				 isSelected() 		? UIConstants.SELECTION_OUTLINE_WIDTH : 0
		);

		if(marker.getType().isArea() && marker.getMarkerPointList().size() > 1) {

			context.polygonFill(
					color,
					UIConstants.AREA_OPACITY,
					xPoints,
					yPoints,
					nPoint
			);

			if(isHighlighted() || isSelected()) {

				context.polygonStroke(
						color,
						lineWidth,
						xPoints,
						yPoints,
						nPoint
				);

			}

		}else if(marker.getType().isTrack()) {

			context.polylineStroke(
					color,
					lineWidth,
					xPoints,
					yPoints,
					nPoint
			);

		}

	}

	private void drawMarkerImage(PaintContext context) {

		if(marker.getMarkerIcon() == null || marker.getMarkerPointList().isEmpty()) {
			return;
		}

		final boolean isDrawSelectionShape = isSelected()
				|| isHighlighted()
				|| (marker.getType().isEntrance()
					&& isEditing()
					&& getChildren()
							.stream()
							.filter(node -> node.isSelected() || node.isHighlighted())
							.findAny()
							.isPresent()
		);

		if(isDrawSelectionShape) {

			final Color color = isSelected()
					? UIConstants.SELECTION_COLOR
					: UIConstants.HIGHLIGHT_COLOR;

			context.ovalGradientLeveled(
					color,
					Color.WHITE,
					point.x(),
					point.y(),
					UIConstants.MARKER_WITH_OUTLINE_SIZE,
					UIConstants.MARKER_WITH_OUTLINE_SIZE
			);

		}

		context.imageLeveled(
				marker.getMarkerIcon().getImage().getImage(),
				point.x(),
				point.y(),
				UIConstants.MARKER_SIZE,
				UIConstants.MARKER_SIZE
		);

	}

	private void drawPoint(PaintContext context, final double x, final double y)	{
		if(!isEditing() || !isTargetingPoint())	{
			return;
		}

		context.ovalFill(
				UIConstants.POINT_COLOR,
				x,
				y,
				UIConstants.MARKER_POINT_SIZE,
				UIConstants.MARKER_POINT_SIZE
		);
	}

	private void drawSuggestedOutline(PaintContext paintContext, Optional<MarkerPoint> suggestedPoint) {

		if(!isEditing() || !isTargetingPoint()) {
			return;
		}

		suggestedPoint.ifPresent(firstPoint -> paintContext.lineStrokeDashed(
				UIConstants.SUGGESTED_OUTLINE_COLOR,
				UIConstants.SUGGESTED_OUTLINE_WIDTH,
				firstPoint.getX(),
				firstPoint.getY(),
				getCursorX(),
				getCursorY(),
				10,
				10
		));

	}

	private void drawMarkerQuality(final PaintContext context)	{

		if(!settingsBean.isShowMarkerQuality())	{
			return;
		}

		if(!marker.getType().isArea()) {
			return;
		}

		if(!isHighlighted())	{
			return;
		}

		if(point == null)	{
			return;
		}

		UIUtils.drawMarkerQuality(
				context,
				point.x(),
				point.y(),
				marker.getEssence(),
				marker.getSubstance(),
				marker.getVitality()
		);

	}

	//

	private void calculateSuggestedPoints() {
		if(marker.getType().isArea())	{
			calculateSuggestedAreaPoints();
		}else if(marker.getType().isTrack())	{
			calculateSuggestedTrackPoint();
		}
	}

	private void calculateSuggestedAreaPoints()	{
		firstSuggestedPoint = MarkerPointSuggestions
				.getNearPoint(
						marker.getMarkerPointList(),
						getCursorX(),
						getCursorY()
				);

		firstSuggestedPoint.ifPresent(firstPoint -> {

			final Collection<MarkerPoint> pointCollectionCopy = MarkerPointSuggestions.getNeighbours(
					marker.getMarkerPointList(),
					firstPoint.getOrderNumber()
			);

			secondSuggestedPoint = MarkerPointSuggestions.getNearPoint(
					pointCollectionCopy,
					getCursorX(),
					getCursorY()
			);

		});
	}

	private void calculateSuggestedTrackPoint()	{
		final List<MarkerPoint> pointForSuggestion = new ArrayList<>();

		marker
				.getMarkerPointList()
				.stream()
				.min(MarkerPoint.MARKER_POINT_COMPARATOR)
				.ifPresent(pointForSuggestion::add);
		marker
				.getMarkerPointList()
				.stream()
				.max(MarkerPoint.MARKER_POINT_COMPARATOR)
				.ifPresent(pointForSuggestion::add);

		firstSuggestedPoint = MarkerPointSuggestions.getNearPoint(pointForSuggestion, getCursorX(), getCursorY());
		secondSuggestedPoint = Optional.empty();
	}

	//

	@Override
	public void merge(@NotNull final Node source) {
		final MarkerNode sourceMarkerNode = (MarkerNode)source;
		marker = sourceMarkerNode.marker;
	}

	private static <T> double[] getPoints(
			@NotNull final Collection<T> pointCollection,
			@NotNull final Function<T, Double> pointPartExtractor
	) {

		final List<T> list = new ArrayList<>(pointCollection);
		final double[] result = new double[list.size()];
		for(int i = 0; i < list.size(); i++) {
			result[i] = pointPartExtractor.apply(list.get(i));
		}
		return result;

	}

	private boolean isMarkerVisible() {
		return isEditing() || (
				marker.isVisible()
				&& !settingsBean.isHideMarkers()
		);
	}

	private boolean isAccessible()  {
		return isEditing() || (
				marker.isVisible()
				&& !settingsBean.getSelectionMode().isMarkerRestricted()
				&& !settingsBean.isHideMarkers()
		);
	}

	private static double getCursorX()	{
		return UIContext.get().getMapViewerWrapper().getCursorWorldX();
	}

	private static double getCursorY()	{
		return UIContext.get().getMapViewerWrapper().getCursorWorldY();
	}

	private static boolean isTargetingPoint()	{
		return UIContext.get().getMapViewerWrapper().isTargetingPoint();
	}

	private static Comparator<Point2D> getClockwiseComparator(@NotNull final Point2D centroid)	{
		return (o1, o2) -> Geometry.ccw(centroid, o2, o1);
	}
}
