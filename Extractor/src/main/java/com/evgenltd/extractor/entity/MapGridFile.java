package com.evgenltd.extractor.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: mapper
 * Author:  Evgeniy Lebedev
 * Created: 25-01-2017 02:18
 */
public class MapGridFile extends CacheFile {

	private int gridBlockVersion;
	private long id;
	private final List<TileInfo> tileSet = new ArrayList<>(); // only raw textures which blended
	private byte[] content;

	public int getGridBlockVersion() {
		return gridBlockVersion;
	}

	public void setGridBlockVersion(int gridBlockVersion) {
		this.gridBlockVersion = gridBlockVersion;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<TileInfo> getTileSet() {
		return tileSet;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public static class TileInfo {
		private String resourcePath;
		private int resourceVersion;
		private int prio; // todo ???

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

		public int getPrio() {
			return prio;
		}

		public void setPrio(int prio) {
			this.prio = prio;
		}

		@Override
		public String toString() {
			return resourcePath;
		}
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append("Id: ").append(id).append("\n");
		sb.append("TileSet: ").append(tileSet).append("\n");
		return sb.toString();
	}
}
