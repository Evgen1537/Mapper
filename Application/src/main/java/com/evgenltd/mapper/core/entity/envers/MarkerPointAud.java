package com.evgenltd.mapper.core.entity.envers;

import com.evgenltd.mapper.core.entity.impl.MarkerPointImpl;
import org.hibernate.envers.RevisionType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Project: mapper
 * Author:  Lebedev
 * Created: 14-07-2016 10:55
 */
@Entity
@Table(name = "marker_points_AUD")
public class MarkerPointAud implements Aud,Serializable {

    @Column(name = "REV")
    @Id
    private Long rev;

    @Column(name = "REVTYPE")
    private RevisionType revType;

    @Id
    private Long id;

    private Double x;

    private Double y;

    @Column(name = "order_number")
    private Long orderNumber;

    @Column(name = "marker_id")
    private Long markerId;

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
		return MarkerPointImpl.class;
	}

	@Override
    public Long getId() {
        return id;
    }

	@Override
    public void setId(final Long id) {
        this.id = id;
    }

    public Double getX() {
        return x;
    }

    public void setX(final Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(final Double y) {
        this.y = y;
    }

    public Long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(final Long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Long getMarkerId() {
        return markerId;
    }

    public void setMarkerId(final Long markerId) {
        this.markerId = markerId;
    }
}
