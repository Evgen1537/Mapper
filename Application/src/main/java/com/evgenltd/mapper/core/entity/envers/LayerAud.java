package com.evgenltd.mapper.core.entity.envers;

import com.evgenltd.mapper.core.entity.impl.LayerImpl;
import com.evgenltd.mapper.core.enums.LayerType;
import com.evgenltd.mapper.core.enums.Visibility;
import org.hibernate.envers.RevisionType;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Project: mapper
 * Author:  Lebedev
 * Created: 14-07-2016 10:50
 */
@Entity
@Table(name = "layers_AUD")
public class LayerAud implements Aud,Serializable {

    @Column(name = "REV")
    @Id
    private Long rev;

    @Column(name = "REVTYPE")
    @Enumerated
    private RevisionType revType;

    @Id
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private LayerType type;

    private Double x;

    private Double y;

    @Enumerated(EnumType.STRING)
    private Visibility visibility;

    @Column(name = "order_number")
    private Long orderNumber;

    @Column(name = "session_path")
    private String sessionPath;

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
		return LayerImpl.class;
	}

	@Override
    public Long getId() {
        return id;
    }

	@Override
    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public LayerType getType() {
        return type;
    }

    public void setType(final LayerType type) {
        this.type = type;
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

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(final Visibility visibility) {
        this.visibility = visibility;
    }

    public Long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(final Long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getSessionPath() {
        return sessionPath;
    }

    public void setSessionPath(final String sessionPath) {
        this.sessionPath = sessionPath;
    }

//    @Override
    public void revoke(@NotNull final EntityManager entityManager) {

    }

//    @Override
    public void accept(@NotNull final EntityManager entityManager) {

    }

//    @Override
    public void insert(@NotNull final EntityManager entityManager)   {

        entityManager
                .createNativeQuery("insert into layers (id,name,type,x,y,visibility,orderNumber,sessionPath) values (:id,:name,:type,:x,:y,:visibility,:orderNumber,:sessionPath)")
                .setParameter("id",id)
                .setParameter("name",name)
                .setParameter("type",type)
                .setParameter("x",x)
                .setParameter("y",y)
                .setParameter("visibility",visibility)
                .setParameter("orderNumber",orderNumber)
                .setParameter("sessionPath",sessionPath)
                .executeUpdate();

    }

//    @Override
    public void update(@NotNull final EntityManager entityManager)   {

        entityManager
                .createNativeQuery("update layers set name = :name, type = :type, x = :x, y = :y, visibility = :visibility, orderNumber = :orderNumber, sessionPath = :sessionPath where id = :id")
                .setParameter("id",id)
                .setParameter("name",name)
                .setParameter("type",type)
                .setParameter("x",x)
                .setParameter("y",y)
                .setParameter("visibility",visibility)
                .setParameter("orderNumber",orderNumber)
                .setParameter("sessionPath",sessionPath)
                .executeUpdate();

    }

//    @Override
    public void delete(@NotNull final EntityManager entityManager)   {

        entityManager
                .createNativeQuery("delete from layers where id = :id")
                .setParameter("id",id)
                .executeUpdate();

    }
}
