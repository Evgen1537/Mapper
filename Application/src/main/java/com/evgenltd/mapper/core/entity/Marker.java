package com.evgenltd.mapper.core.entity;

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
@Entity
@Table(name = "markers")
@Audited
public class Marker implements Movable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private MarkerType type;

	@ManyToOne
	@JoinColumn(name = "marker_icon_id")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private MarkerIcon markerIcon;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "layer_id")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private Layer layer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "exit_id")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private Layer exit;

	private String essence;
	private String substance;
	private String vitality;

	private String comment;

	@OneToMany(mappedBy = "marker", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@OrderBy("orderNumber")
	private Set<MarkerPoint> markerPointList;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MarkerType getType() {
		return type;
	}

	public void setType(MarkerType type) {
		this.type = type;
	}

	public MarkerIcon getMarkerIcon() {
		return markerIcon;
	}

	public Long getMarkerIconId()	{
		return markerIcon == null
				? null
				: markerIcon.getId();
	}

	public void setMarkerIcon(MarkerIcon markerIcon) {
		this.markerIcon = markerIcon;
	}

	public Layer getLayer() {
		return layer;
	}

	public Long getLayerId()	{
		return layer == null
				? null
				: layer.getId();
	}

	public void setLayer(Layer layer) {
		this.layer = layer;
	}

	public Layer getExit() {
		return exit;
	}

	public Long getExitId()	{
		return exit == null
				? null
				: exit.getId();
	}

	public void setExit(Layer exit) {
		this.exit = exit;
	}

	public String getEssence() {
		return essence;
	}

	public void setEssence(String essence) {
		this.essence = essence;
	}

	public String getSubstance() {
		return substance;
	}

	public void setSubstance(String substance) {
		this.substance = substance;
	}

	public String getVitality() {
		return vitality;
	}

	public void setVitality(String vitality) {
		this.vitality = vitality;
	}

	public String getName()	{
		if(type.isArea())	{
			return markerIcon.getName();
		}else {
			return type.getLabel();
		}
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

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

	public boolean isVisible()  {
		return !getLayerVisibility(layer).isNone() || !getLayerVisibility(exit).isNone();
	}

	private Visibility getLayerVisibility(final Layer layer) {
		return layer == null
				? Visibility.FULL
				: layer.getVisibility();
	}

	public Collection<MarkerPoint> getMarkerPointList() {
		return markerPointList;
	}

	public void setMarkerPointList(Set<MarkerPoint> markerPointList) {
		this.markerPointList = markerPointList;
	}
}
