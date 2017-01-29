package com.evgenltd.extractor.entity;


import haven.Coord;

import java.util.HashMap;
import java.util.Map;

/**
 * Project: mapper
 * Author:  Evgeniy Lebedev
 * Created: 25-01-2017 01:46
 */
public class SegmentFile extends CacheFile {

	private int segmentBlockVersion;
	private long id;
	private final Map<Coord,Long> coordinatesToId = new HashMap<>();

	public int getSegmentBlockVersion() {
		return segmentBlockVersion;
	}

	public void setSegmentBlockVersion(int segmentBlockVersion) {
		this.segmentBlockVersion = segmentBlockVersion;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Map<Coord, Long> getCoordinatesToId() {
		return coordinatesToId;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Id: ").append(id).append("\n");
		sb.append("Map coordinates and ids: \n");
		coordinatesToId.forEach((key,value) -> sb.append(String.format("%s,%s,%s\n",key.x,key.y,value)));
		return sb.toString();
	}
}
