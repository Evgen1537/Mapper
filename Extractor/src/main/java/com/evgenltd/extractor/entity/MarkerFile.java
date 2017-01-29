package com.evgenltd.extractor.entity;

import haven.Coord;

import java.awt.*;
import java.util.Objects;

/**
 * Project: mapper
 * Author:  Evgeniy Lebedev
 * Created: 25-01-2017 00:06
 */
public class MarkerFile {

	private int version;
	private long segmentId;
	private Coord coordinates;
	private String name;
	private Type type;
	private Color color;
	private long objectId;
	private String resourcePath;
	private int resourceVersion;

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public long getSegmentId() {
		return segmentId;
	}

	public void setSegmentId(long segmentId) {
		this.segmentId = segmentId;
	}

	public Coord getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Coord coordinates) {
		this.coordinates = coordinates;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public long getObjectId() {
		return objectId;
	}

	public void setObjectId(long objectId) {
		this.objectId = objectId;
	}

	public String getResourcePath() {
		return resourcePath;
	}

	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}

	public int getResourceVersion() {
		return resourceVersion;
	}

	public void setResourceVersion(int resourceVersion) {
		this.resourceVersion = resourceVersion;
	}

	public enum Type {
		PLAYER_DEFINED('p'), SERVER_DEFINED('s');

		private char code;

		Type(char code) {
			this.code = code;
		}

		public char getCode() {
			return code;
		}

		public static Type fromCode(final char typeCode) {

			for (final Type type : values()) {
				if (type.getCode() == typeCode) {
					return type;
				}
			}

			throw new IllegalArgumentException(String.format("Unsupported marker type [%resourcePath]", typeCode));

		}

	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("SegmentId: ").append(segmentId).append("\n");
		sb.append("Coordinates: ").append(String.format("(%s,%s)",coordinates.x,coordinates.y)).append("\n");
		sb.append("Name: ").append(name).append("\n");
		sb.append("Type: ").append(type).append("\n");
		if (Objects.equals(type, Type.PLAYER_DEFINED)) {
			sb.append("Color: ").append(color).append("\n");
		} else if (Objects.equals(type, Type.SERVER_DEFINED)) {
			sb.append("ObjectId: ").append(objectId).append("\n");
			sb.append("Resource: ").append(resourcePath).append("\n");
		}
		return sb.toString();
	}
}
