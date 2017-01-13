package com.evgenltd.mapper.core.entity;

import javax.persistence.*;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 13-06-2016 01:21
 */
@Entity
@Table(name = "marker_icons")
public class MarkerIcon {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "image_id")
	private Image image;

	public MarkerIcon() {
	}

	public MarkerIcon(String name, javafx.scene.image.Image image) {
		this.name = name;
		this.image = new Image(image);
	}

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

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		MarkerIcon that = (MarkerIcon)o;

		return id != null ? id.equals(that.id) : that.id == null;

	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}
}
