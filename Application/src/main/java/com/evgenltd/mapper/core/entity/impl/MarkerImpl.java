package com.evgenltd.mapper.core.entity.impl;

import com.evgenltd.mapper.core.entity.Layer;
import com.evgenltd.mapper.core.entity.Marker;
import com.evgenltd.mapper.core.entity.MarkerIcon;
import com.evgenltd.mapper.core.entity.MarkerPoint;
import com.evgenltd.mapper.core.enums.MarkerType;
import com.evgenltd.mapper.core.enums.Visibility;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 13-06-2016 01:24
 */
@Entity(name = "Marker")
@Table(name = "markers")
@Audited
public class MarkerImpl implements Marker {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private MarkerType type;

	@ManyToOne(targetEntity = MarkerIconImpl.class)
	@JoinColumn(name = "marker_icon_id")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private MarkerIcon markerIcon;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = LayerImpl.class)
	@JoinColumn(name = "layer_id")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private Layer layer;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = LayerImpl.class)
	@JoinColumn(name = "exit_id")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private Layer exit;

	private String essence;
	private String substance;
	private String vitality;

	private String comment;

	@OneToMany(mappedBy = "marker", cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = MarkerPointImpl.class)
	@OrderBy("orderNumber")
	private Set<MarkerPoint> markerPointList;

	@Override
	public Long getId() {
		return id;
	}
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public MarkerType getType() {
		return type;
	}
	@Override
	public void setType(MarkerType type) {
		this.type = type;
	}

	@Override
	public MarkerIcon getMarkerIcon() {
		return markerIcon;
	}
	@Override
	public Long getMarkerIconId()	{
		return markerIcon == null
				? null
				: markerIcon.getId();
	}
	@Override
	public void setMarkerIcon(MarkerIcon markerIcon) {
		this.markerIcon = markerIcon;
	}

	@Override
	public Layer getLayer() {
		return layer;
	}
	@Override
	public Long getLayerId()	{
		return layer == null
				? null
				: layer.getId();
	}
	@Override
	public void setLayer(Layer layer) {
		this.layer = layer;
	}
	
	@Override
	public Layer getExit() {
		return exit;
	}
	@Override
	public Long getExitId()	{
		return exit == null
				? null
				: exit.getId();
	}
	@Override
	public void setExit(Layer exit) {
		this.exit = exit;
	}

	@Override
	public String getEssence() {
		return essence;
	}
	@Override
	public void setEssence(String essence) {
		this.essence = essence;
	}

	@Override
	public String getSubstance() {
		return substance;
	}
	@Override
	public void setSubstance(String substance) {
		this.substance = substance;
	}

	@Override
	public String getVitality() {
		return vitality;
	}
	@Override
	public void setVitality(String vitality) {
		this.vitality = vitality;
	}

	@Override
	public String getName()	{
		if(type.isArea())	{
			return markerIcon.getName();
		}else {
			return type.getLabel();
		}
	}

	@Override
	public String getComment() {
		return comment;
	}
	@Override
	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public Visibility getVisibility() {

		final Visibility layer = getLayerVisibility(this.layer);
		final Visibility exit = getLayerVisibility(this.exit);

		if(layer.isNone() || exit.isNone()) {
			return Visibility.NONE;
		}else if(layer.isPartly() || exit.isPartly())   {
			return Visibility.PARTLY;
		}else {
			return Visibility.FULL;
		}

	}
	@Override
	public boolean isVisible()  {
		return !getLayerVisibility(layer).isNone() || !getLayerVisibility(exit).isNone();
	}

	private Visibility getLayerVisibility(final Layer layer) {
		return layer == null
				? Visibility.FULL
				: layer.getVisibility();
	}

	@Override
	public Collection<MarkerPoint> getMarkerPointList() {
		return markerPointList;
	}
	@Override
	public void setMarkerPointList(Set<MarkerPoint> markerPointList) {
		this.markerPointList = markerPointList;
	}
}
