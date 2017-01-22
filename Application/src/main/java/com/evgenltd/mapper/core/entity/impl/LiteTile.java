package com.evgenltd.mapper.core.entity.impl;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.entity.Layer;
import com.evgenltd.mapper.core.entity.Picture;
import com.evgenltd.mapper.core.entity.Tile;
import com.evgenltd.mapper.mapviewer.common.ZLevel;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;
import javax.persistence.*;
import java.lang.ref.SoftReference;
import java.util.List;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 17-08-2016 10:36
 */
@Entity
@Table(name = "tiles")
@Immutable
public class LiteTile implements Tile {

	@Id
	private Long id;

	private Double x;

	private Double y;

	@Enumerated(EnumType.STRING)
	private ZLevel z;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = LayerImpl.class)
	@JoinColumn(name = "layer_id")
	private Layer layer;

	@Column(name = "image_id")
	private Long imageId;

	private String hash;

	@Transient
	private SoftReference<Picture> image = new SoftReference<>(null);

	public static LiteTile fromTile(@NotNull final Tile tile)	{
		final LiteTile liteTile = new LiteTile();
		liteTile.id = tile.getId();
		liteTile.x = tile.getX();
		liteTile.y = tile.getY();
		liteTile.z = tile.getZ();
		liteTile.layer = tile.getLayer();
		liteTile.imageId = tile.getImageId();
		liteTile.hash = tile.getHash();
		liteTile.image = new SoftReference<>(tile.getImageEntity());
		return liteTile;
	}

	@Override
	public Long getId() {
		return id;
	}
	@Override
	public void setId(Long id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Double getX() {
		return x;
	}
	@Override
	public void setX(Double x) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Double getY() {
		return y;
	}
	@Override
	public void setY(Double y) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ZLevel getZ() {
		return z;
	}
	@Override
	public void setZ(ZLevel z) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Layer getLayer() {
		return layer;
	}
	@Override
	public void setLayer(Layer layer) {
		throw new UnsupportedOperationException();
	}

	@Override
	public byte[] getContent() {
		throw new UnsupportedOperationException();
	}
	@Override
	public Long getImageId() {
		return imageId;
	}

	@Override
	public Picture getImageEntity() {
		Picture image = this.image.get();
		if(image == null)	{
			image = Context
					.get()
					.getLoader()
					.loadImage(imageId);
			this.image = new SoftReference<>(image);
			return image;
		}
		return image;
	}
	@Override
	public void setImageEntity(Picture picture) {
		this.image = new SoftReference<>(picture);
	}

	@Override
	public Image getImage() {
		return getImageEntity().getImage();
	}
	@Override
	public void setImage(Image image) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getHash() {
		return hash;
	}
	@Override
	public void setHash(String hash) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Point2D> getLowLevelTilePointList() {
		throw new UnsupportedOperationException();
	}
	@Override
	public void setLowLevelTilePointList(List<Point2D> lowLevelTilePointList) {
		throw new UnsupportedOperationException();
	}

}
