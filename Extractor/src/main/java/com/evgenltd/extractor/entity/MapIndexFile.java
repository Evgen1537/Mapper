package com.evgenltd.extractor.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: mapper
 * Author:  Evgeniy Lebedev
 * Created: 24-01-2017 23:35
 */
public class MapIndexFile extends CacheFile {

	private int segmentBlockVersion;
	private final List<Long> segmentIdList = new ArrayList<>();
	private final List<MarkerFile> hafenMarkerList = new ArrayList<>();

	public int getSegmentBlockVersion() {
		return segmentBlockVersion;
	}

	public void setSegmentBlockVersion(int segmentBlockVersion) {
		this.segmentBlockVersion = segmentBlockVersion;
	}

	public List<Long> getSegmentIdList() {
		return segmentIdList;
	}

	public List<MarkerFile> getHafenMarkerList() {
		return hafenMarkerList;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append("Segments: ").append(segmentIdList).append("\n");
		sb.append("Markers: [\n");
		for (final MarkerFile markerFile : hafenMarkerList) {
			sb.append("====================\n");
			sb.append(markerFile);
		}
		sb.append("]\n");
		return sb.toString();
	}
}
