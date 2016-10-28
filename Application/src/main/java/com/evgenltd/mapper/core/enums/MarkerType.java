package com.evgenltd.mapper.core.enums;

import java.util.Objects;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 13-06-2016 01:21
 */
public enum MarkerType {
	AREA("Area"),TRACK("Track"),ENTRANCE("Entrance");

	private String label;

	MarkerType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public boolean isArea()	{
		return Objects.equals(this,AREA);
	}

	public boolean isTrack()	{
		return Objects.equals(this,TRACK);
	}

	public boolean isEntrance()	{
		return Objects.equals(this,ENTRANCE);
	}
}
