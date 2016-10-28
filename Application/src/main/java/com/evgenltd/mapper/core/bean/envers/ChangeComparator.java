package com.evgenltd.mapper.core.bean.envers;

import com.evgenltd.mapper.core.entity.envers.*;
import org.hibernate.envers.RevisionType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 17-07-2016 13:01
 */
@ParametersAreNonnullByDefault
public class ChangeComparator implements Comparator<Aud> {

	private static final List<Class<?>> ADD_CLASS_ORDER = Arrays.asList(
			LayerAud.class,
			TileAud.class,
			MarkerAud.class,
			MarkerPointAud.class
	);
	private static final List<Class<?>> DEL_CLASS_ORDER = Arrays.asList(
			MarkerPointAud.class,
			MarkerAud.class,
			TileAud.class,
			LayerAud.class
	);

	@Override
	public int compare(Aud first, Aud second) {
		final int firstOrderIndex = resolveOrder(first.getClass(), first.getRevType());
		final int secondOrderIndex = resolveOrder(second.getClass(), second.getRevType());
		return Integer.compare(firstOrderIndex, secondOrderIndex);
	}

	private int resolveOrder(final Class<?> changeClass, final RevisionType changeType)	{
		switch(changeType)	{
			case ADD:
				return ADD_CLASS_ORDER.indexOf(changeClass);
			case MOD:
				return ADD_CLASS_ORDER.indexOf(changeClass) + 10;
			case DEL:
				return DEL_CLASS_ORDER.indexOf(changeClass) + 20;
			default:
				throw new IllegalArgumentException(String.format("Unknown revisionType=[%s]",changeType));
		}
	}
}
