package com.evgenltd.mapper.core.entity.envers;

import com.evgenltd.mapper.core.entity.impl.MarkerImpl;
import com.evgenltd.mapper.core.enums.MarkerType;
import org.hibernate.envers.RevisionType;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Project: mapper
 * Author:  Lebedev
 * Created: 14-07-2016 10:53
 */
@Entity
@Table(name = "markers_AUD")
public class MarkerAud implements Aud,Serializable {

    @Column(name = "REV")
    @Id
    private Long rev;

    @Column(name = "REVTYPE")
    private RevisionType revType;

    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    private MarkerType type;

    @Column(name = "marker_icon_id")
    private Long markerIconId;

    @Column(name = "layer_id")
    private Long layerId;

    @Column(name = "exit_id")
    private Long exitId;

    private String essence;
    private String substance;
    private String vitality;

    private String comment;

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
		return MarkerImpl.class;
	}

	@Override
    public Long getId() {
        return id;
    }

	@Override
    public void setId(final Long id) {
        this.id = id;
    }

    public MarkerType getType() {
        return type;
    }

    public void setType(final MarkerType type) {
        this.type = type;
    }

    public Long getMarkerIconId() {
        return markerIconId;
    }

    public void setMarkerIconId(final Long markerIconId) {
        this.markerIconId = markerIconId;
    }

    public Long getLayerId() {
        return layerId;
    }

    public void setLayerId(final Long layerId) {
        this.layerId = layerId;
    }

    public Long getExitId() {
        return exitId;
    }

    public void setExitId(final Long exitId) {
        this.exitId = exitId;
    }

    public String getEssence() {
        return essence;
    }

    public void setEssence(final String essence) {
        this.essence = essence;
    }

    public String getSubstance() {
        return substance;
    }

    public void setSubstance(final String substance) {
        this.substance = substance;
    }

    public String getVitality() {
        return vitality;
    }

    public void setVitality(final String vitality) {
        this.vitality = vitality;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }
}
