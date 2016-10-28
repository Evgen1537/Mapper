package com.evgenltd.mapper.core.bean;

import com.evgenltd.mapper.JavaFXSpringRunner;
import com.evgenltd.mapper.core.TestContextConfig;
import com.evgenltd.mapper.core.TestUtils;
import com.evgenltd.mapper.core.entity.Layer;
import com.evgenltd.mapper.core.entity.LiteTile;
import com.evgenltd.mapper.core.entity.Tile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 18-08-2016 22:46
 */
@RunWith(JavaFXSpringRunner.class)
@ContextConfiguration(classes = TestContextConfig.class, loader = AnnotationConfigContextLoader.class)
public class TileBeanTest {

	@Autowired
	private CommonDao commonDao;

	@Test
	public void testLiteTilePersistence()	{
		final Layer layer = new Layer();
		commonDao.insert(layer);

		final Tile tile = TestUtils.createTile(0, 0, 1, layer);
		commonDao.insert(tile);

		final LiteTile liteTile = commonDao.find(LiteTile.class,tile.getId());
		System.err.println("123");

	}

}
