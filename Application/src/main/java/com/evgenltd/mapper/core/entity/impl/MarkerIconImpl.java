package com.evgenltd.mapper.core.entity.impl;

import com.evgenltd.mapper.core.entity.MarkerIcon;
import com.evgenltd.mapper.core.entity.Picture;

import javax.persistence.*;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 13-06-2016 01:21
 */
@Entity(name = "MarkerIcon")
@Table(name = "marker_icons")
public class MarkerIconImpl implements MarkerIcon {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@OneToOne(cascade = CascadeType.ALL, targetEntity = PictureImpl.class)
	@JoinColumn(name = "image_id")
	private Picture image;

	@Override
	public Long getId() {
		return id;
	}
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return name;
	}
	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Picture getImage() {
		return image;
	}
	@Override
	public void setImage(Picture image) {
		this.image = image;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		MarkerIconImpl that = (MarkerIconImpl)o;

		return id != null ? id.equals(that.id) : that.id == null;

	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

}
