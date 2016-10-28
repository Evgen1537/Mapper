package com.evgenltd.mapper.mapviewer.common;

import javafx.event.EventHandler;
import javafx.event.EventType;

/**
 * Project: MapperPrototype
 * Author:  Evgeniy
 * Created: 08-06-2016 23:44
 */
class EventProperties {

	public static final EventType<? extends MapViewerEvent> MAP_VIEWER_ROOT = new EventType<>("MAP_VIEWER_ROOT");
	public static final EventType<? extends MapViewerEvent> HIGHLIGHT_EVENT = new EventType<>("HIGHLIGHT_EVENT");
	public static final EventType<? extends MapViewerEvent> SELECTION_EVENT = new EventType<>("SELECTION_EVENT");
	public static final EventType<? extends MapViewerEvent> MOVE_EVENT = new EventType<>("MOVE_EVENT");
	public static final EventType<? extends MapViewerEvent> VIEWPORT_EVENT = new EventType<>("VIEWPORT_EVENT");

	private EventHandler<MapViewerEvent> onHighlightChanged = event -> {};
	private EventHandler<MapViewerEvent> onSelectionChanged = event -> {};
	private EventHandler<MoveEvent> onMoved = event -> {};
	private EventHandler<MapViewerEvent> onViewportChanged = event -> {};

	public EventHandler<? extends MapViewerEvent> getOnHighlightChanged() {
		return onHighlightChanged;
	}

	public void setOnHighlightChanged(EventHandler<MapViewerEvent> onHighlightChanged) {
		if(onHighlightChanged == null)	{
			this.onHighlightChanged = event -> {};
		}else {
			this.onHighlightChanged = onHighlightChanged;
		}
	}

	public EventHandler<? extends MapViewerEvent> getOnSelectionChanged() {
		return onSelectionChanged;
	}

	public void setOnSelectionChanged(EventHandler<MapViewerEvent> onSelectionChanged) {
		if(onSelectionChanged == null)	{
			this.onSelectionChanged = event -> {};
		}else {
			this.onSelectionChanged = onSelectionChanged;
		}
	}

	public EventHandler<? extends MapViewerEvent> getOnMoved() {
		return onMoved;
	}

	public void setOnMoved(EventHandler<MoveEvent> onMoved) {
		if(onMoved == null)	{
			this.onMoved = event -> {};
		}else {
			this.onMoved = onMoved;
		}
	}

	public EventHandler<? extends MapViewerEvent> getOnViewportChanged() {
		return onViewportChanged;
	}

	public void setOnViewportChanged(EventHandler<MapViewerEvent> onViewportChanged) {
		if(onViewportChanged == null)	{
			this.onViewportChanged = event -> {};
		}else {
			this.onViewportChanged = onViewportChanged;
		}
	}

	void fireHighlightChanged()	{
		onHighlightChanged.handle(new MapViewerEvent(HIGHLIGHT_EVENT));
	}

	void fireSelectionChanged()	{
		onSelectionChanged.handle(new MapViewerEvent(SELECTION_EVENT));
	}

	void fireMoved(final double worldDeltaX, final double worldDeltaY)	{
		onMoved.handle(new MoveEvent(worldDeltaX,worldDeltaY));
	}

	void fireViewportChanged()	{
		onViewportChanged.handle(new MapViewerEvent(VIEWPORT_EVENT));
	}

}
