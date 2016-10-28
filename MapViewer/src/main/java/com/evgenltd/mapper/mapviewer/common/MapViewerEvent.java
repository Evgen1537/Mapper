package com.evgenltd.mapper.mapviewer.common;

import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventType;

/**
 * Project: MapperPrototype
 * Author:  Evgeniy
 * Created: 08-06-2016 23:10
 */
public class MapViewerEvent extends Event {
	public MapViewerEvent(@NamedArg("eventType") EventType<? extends Event> eventType) {
		super(eventType);
	}
}
