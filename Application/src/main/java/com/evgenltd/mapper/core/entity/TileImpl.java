package com.evgenltd.mapper.core.entity;

import com.evgenltd.mapper.mapviewer.common.ZLevel;
import javafx.geometry.Point2D;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;
import java.util.List;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 18-08-2016 00:07
 */
@Entity(name = "Tile")
@Table(name = "tiles")
@Audited
public class TileImpl implements Tile {

	@Id
	@GeneratedValue
	private Long id;

	private Double x;

	private Double y;

	@Enumerated(EnumType.STRING)
	private ZLevel z;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "layer_id")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private Layer layer;

	@ManyToOne(
			fetch = FetchType.LAZY,
			cascade = {
					CascadeType.PERSIST,
					CascadeType.MERGE,
					CascadeType.REFRESH,
					CascadeType.DETACH
			}
	)
	@JoinColumn(name = "image_id")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private Image image = new Image();

	private String hash;

	@Transient
	@NotAudited
	private List<Point2D> lowLevelTilePointList;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Double getX() {
		return x;
	}

	@Override
	public void setX(Double x) {
		this.x = x;
	}

	@Override
	public Double getY() {
		return y;
	}

	@Override
	public void setY(Double y) {
		this.y = y;
	}

	@Override
	public ZLevel getZ() {
		return z;
	}

	@Override
	public void setZ(ZLevel z) {
		this.z = z;
	}

	@Override
	public Layer getLayer() {
		return layer;
	}

	@Override
	public void setLayer(Layer layer) {
		this.layer = layer;
	}

	@Override
	public byte[] getContent() {
		return image.getContent();
	}

	@Override
	public javafx.scene.image.Image getImage() {
		return image.getImage();
	}

	@Override
	public Long getImageId()	{
		return image.getId();
	}

	@Override
	public Image getImageEntity() {
		return image;
	}

	@Override
	public void setImage(javafx.scene.image.Image image) {
		this.image.setImage(image);
	}

	@Override
	public String getHash() {
		return hash;
	}

	@Override
	public void setHash(String hash) {
		this.hash = hash;
	}

	@Override
	public List<Point2D> getLowLevelTilePointList() {
		return lowLevelTilePointList;
	}

	@Override
	public void setLowLevelTilePointList(List<Point2D> lowLevelTilePointList) {
		this.lowLevelTilePointList = lowLevelTilePointList;
	}
}
