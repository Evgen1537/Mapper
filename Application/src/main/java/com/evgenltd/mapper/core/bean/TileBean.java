package com.evgenltd.mapper.core.bean;

import com.evgenltd.mapper.core.entity.Tile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 19-06-2016 12:02
 */
@Component
@ParametersAreNonnullByDefault
public class TileBean extends AbstractBean {

	@Transactional
	public void update(final Tile tile)	{

		if(tile.getId() == null)	{
			getEntityManager().persist(tile);
		}else {
			getEntityManager().merge(tile);
		}

	}

	private void delete(final Tile tile)	{

		getEntityManager().remove(
				getEntityManager().merge(tile)
		);

	}

}
