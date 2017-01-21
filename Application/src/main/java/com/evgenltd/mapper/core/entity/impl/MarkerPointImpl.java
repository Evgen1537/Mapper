package com.evgenltd.mapper.core.entity.impl;

import com.evgenltd.mapper.core.entity.Marker;
import com.evgenltd.mapper.core.entity.MarkerPoint;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 13-06-2016 01:23
 */
@Entity(name = "MarkerPoint")
@Table(name ="marker_points")
@Audited
public class MarkerPointImpl implements MarkerPoint {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Double x;

	private Double y;

	@Column(name = "order_number")
	private Long orderNumber;

	@ManyToOne(targetEntity = MarkerImpl.class)
	@JoinColumn(name = "marker_id")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private Marker marker;

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
	public Long getOrderNumber() {
		return orderNumber;
	}
	@Override
	public void setOrderNumber(final Long orderNumber) {
		this.orderNumber = orderNumber;
	}

	@Override
	public Marker getMarker() {
		return marker;
	}
	@Override
	public void setMarker(Marker marker) {
		this.marker = marker;
	}

}
