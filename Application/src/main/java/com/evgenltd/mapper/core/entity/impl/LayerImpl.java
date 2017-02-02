package com.evgenltd.mapper.core.entity.impl;

import com.evgenltd.mapper.core.entity.Layer;
import com.evgenltd.mapper.core.entity.Tile;
import com.evgenltd.mapper.core.enums.LayerType;
import com.evgenltd.mapper.core.enums.Visibility;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 13-06-2016 01:14
 */
@Entity(name = "Layer")
@Table(name = "layers")
@Audited
public class LayerImpl implements Layer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@Enumerated(EnumType.STRING)
	private LayerType type = LayerType.SESSION;

	private Double x = 0D;

	private Double y = 0D;

	@Enumerated(EnumType.STRING)
	private Visibility visibility = Visibility.FULL;

	@Column(name = "order_number")
	private Long orderNumber;

	@Column(name = "session_path")
	private String sessionPath;

	private Integer preventRemove;

	@OneToMany(
			mappedBy = "layer",
			fetch = FetchType.LAZY,
			cascade = {
					CascadeType.PERSIST,
					CascadeType.MERGE,
					CascadeType.DETACH,
					CascadeType.REMOVE
			},
			targetEntity = TileImpl.class
	)
	private Set<Tile> tileSet = new HashSet<>();

	@Override
	public Long getId() {
		return id;
	}
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return name;
	}
	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public LayerType getType() {
		return type;
	}
	@Override
	public void setType(LayerType type) {
		this.type = type;
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
	public Visibility getVisibility() {
		return visibility;
	}
	@Override
	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
	}

	@Override
	public Long getOrderNumber() {
		return orderNumber;
	}

	@Override
	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}

	@Override
	public String getSessionPath() {
		return sessionPath;
	}
	@Override
	public void setSessionPath(String sessionPath) {
		this.sessionPath = sessionPath;
	}

	@Override
	public Boolean getPreventRemove() {
		return preventRemove != null && preventRemove == 1;
	}
	@Override
	public void setPreventRemove(Boolean preventRemove) {
		this.preventRemove = preventRemove ? 1 : 0;
	}

	@Override
	public Set<Tile> getTileSet() {
		return tileSet;
	}
	@Override
	public void setTileSet(Set<Tile> tileSet) {
		this.tileSet = tileSet;
	}
}
