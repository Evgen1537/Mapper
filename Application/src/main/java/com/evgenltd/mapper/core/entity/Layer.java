package com.evgenltd.mapper.core.entity;

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
@Entity
@Table(name = "layers")
@Audited
public class Layer implements Ordered,Movable {

	@Id
	@GeneratedValue
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LayerType getType() {
		return type;
	}

	public void setType(LayerType type) {
		this.type = type;
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

	public Visibility getVisibility() {
		return visibility;
	}

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

	public String getSessionPath() {
		return sessionPath;
	}

	public void setSessionPath(String sessionPath) {
		this.sessionPath = sessionPath;
	}

	public Set<Tile> getTileSet() {
		return tileSet;
	}

	public void setTileSet(Set<Tile> tileSet) {
		this.tileSet = tileSet;
	}
}
