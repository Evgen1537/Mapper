package com.evgenltd.mapper.core.entity.dto;

import com.evgenltd.mapper.core.enums.LayerType;
import com.evgenltd.mapper.core.enums.Visibility;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 14-06-2016 03:18
 */
public class LayerDto {
	private Long id;
	private String name;
	private String visibility;
	private String type;
	private Long tileCount;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Visibility getVisibility() {
		return Visibility.valueOf(visibility);
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	public LayerType getType() {
		return LayerType.valueOf(type);
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getTileCount() {
		return tileCount;
	}

	public void setTileCount(Long tileCount) {
		this.tileCount = tileCount;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		LayerDto layerDto = (LayerDto)o;

		return id.equals(layerDto.id);

	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
}
