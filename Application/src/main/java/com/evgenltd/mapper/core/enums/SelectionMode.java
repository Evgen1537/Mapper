package com.evgenltd.mapper.core.enums;

import java.util.Objects;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 17-07-2016 22:21
 */
public enum SelectionMode {
	ALL,MARKER,LAYER;

	public boolean isMarkerRestricted()	{
		return Objects.equals(this,LAYER);
	}

	public boolean isLayerRestricted()	{
		return Objects.equals(this,MARKER);
	}
}
