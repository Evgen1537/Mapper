package com.evgenltd.mapper.core.rule;

import com.evgenltd.mapper.core.entity.Ordered;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 20-06-2016 12:14
 */
public class Reordering {

	public static void bringForward(
			@NotNull List<? extends Ordered> targetEntityList,
			@NotNull List<? extends Ordered> affectedEntityList
	)	{

		targetEntityList.forEach(entity -> incrementOrderNumber(entity, affectedEntityList.size()));
		affectedEntityList.forEach(entity -> decrementOrderNumber(entity, targetEntityList.size()));

	}

	public static void sendBackward(
			@NotNull List<? extends Ordered> targetEntityList,
			@NotNull List<? extends Ordered> affectedEntityList
	)	{
		targetEntityList.forEach(entity -> decrementOrderNumber(entity, affectedEntityList.size()));
		affectedEntityList.forEach(entity -> incrementOrderNumber(entity, targetEntityList.size()));
	}

	private static void incrementOrderNumber(@NotNull Ordered entity, final long delta)	{
		entity.setOrderNumber(entity.getOrderNumber() + delta);
	}

	private static void decrementOrderNumber(@NotNull Ordered entity, final long delta)	{
		entity.setOrderNumber(entity.getOrderNumber() - delta);
	}

	public static <T extends Ordered> void insertOrdered(@NotNull Collection<T> sourceEntityList, @NotNull final T insertEntity)  {
		sourceEntityList
				.stream()
				.filter(higherOrdered -> higherOrdered.getOrderNumber() >= insertEntity.getOrderNumber())
				.forEach(higherOrdered -> incrementOrderNumber(higherOrdered, 1));

		sourceEntityList.add(insertEntity);
	}

	public static <T extends Ordered> void removeOrdered(@NotNull Collection<T> sourceEntityList, @NotNull final T removeEntity)	{
		if(!sourceEntityList.remove(removeEntity)) {
			return;
		}
		sourceEntityList
				.stream()
				.filter(other -> other.getOrderNumber() > removeEntity.getOrderNumber())
				.forEach(other -> decrementOrderNumber(other,1));
	}
}
