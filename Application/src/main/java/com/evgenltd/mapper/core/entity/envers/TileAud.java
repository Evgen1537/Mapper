package com.evgenltd.mapper.core.entity.envers;

import com.evgenltd.mapper.core.entity.impl.TileImpl;
import com.evgenltd.mapper.mapviewer.common.ZLevel;
import org.hibernate.envers.RevisionType;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Project: mapper
 * Author:  Lebedev
 * Created: 14-07-2016 10:52
 */
@Entity
@Table(name = "tiles_AUD")
public class TileAud implements Aud,Serializable {

    @Column(name = "REV")
    @Id
    private Long rev;

    @Column(name = "REVTYPE")
    private RevisionType revType;

	@Id
	private Long id;

	private Double x;

	private Double y;

	@Enumerated(EnumType.STRING)
	private ZLevel z;

	@Column(name = "layer_id")
	private Long layerId;

	@Column(name = "image_id")
	private Long imageId;

	private String hash;

    @Override
    public Long getRev() {
        return rev;
    }

    @Override
    public void setRev(final Long rev) {
        this.rev = rev;
    }

    @Override
    public RevisionType getRevType() {
        return revType;
    }

    @Override
    public void setRevType(final RevisionType revType) {
        this.revType = revType;
    }

	@Override
	public Class<?> getTargetClass() {
		return TileImpl.class;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public Double getX() {
		return x;
	}

	public void setX(Double x) {
		this.x = x;
	}

	public Double getY() {
		return y;
	}

	public void setY(Double y) {
		this.y = y;
	}

	public ZLevel getZ() {
		return z;
	}

	public void setZ(ZLevel z) {
		this.z = z;
	}

	public Long getLayerId() {
		return layerId;
	}

	public void setLayerId(Long layerId) {
		this.layerId = layerId;
	}

	public Long getImageId() {
		return imageId;
	}

	public void setImageId(Long imageId) {
		this.imageId = imageId;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}
}
