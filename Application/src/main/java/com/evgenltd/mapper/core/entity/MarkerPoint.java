package com.evgenltd.mapper.core.entity;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;
import java.util.Comparator;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 13-06-2016 01:23
 */
@Entity
@Table(name ="marker_points")
@Audited
public class MarkerPoint implements Ordered {

	public static final Comparator<MarkerPoint> MARKER_POINT_COMPARATOR = (o1, o2) -> o1.getOrderNumber().compareTo(o2.getOrderNumber());

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Double x;

	private Double y;

	@Column(name = "order_number")
	private Long orderNumber;

	@ManyToOne
	@JoinColumn(name = "marker_id")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private Marker marker;

	public MarkerPoint() {
	}

	public MarkerPoint(Double x, Double y, Long orderNumber, Marker marker) {
		this.x = x;
		this.y = y;
		this.orderNumber = orderNumber;
		this.marker = marker;
	}

	public Long getId() {
		return id;
	}

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

	@Override
	public Long getOrderNumber() {
		return orderNumber;
	}

	@Override
	public void setOrderNumber(final Long orderNumber) {
		this.orderNumber = orderNumber;
	}

	public Marker getMarker() {
		return marker;
	}

	public void setMarker(Marker marker) {
		this.marker = marker;
	}
}
