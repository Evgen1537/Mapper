package com.evgenltd.extractor.entity;

import java.io.File;

/**
 * Project: mapper
 * Author:  Evgeniy Lebedev
 * Created: 24-01-2017 23:35
 */
public class CacheFile {
	private File file;
	private int version;
	private String cacheId;
	private String name;

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getCacheId() {
		return cacheId;
	}

	public void setCacheId(String cacheId) {
		this.cacheId = cacheId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("File: ").append(file.getAbsolutePath()).append("\n");
		sb.append("CacheId: ").append(cacheId).append("\n");
		sb.append("Name: ").append(name).append("\n");
		return sb.toString();
	}
}
