package com.evgenltd.mapper.core.bean;

import com.evgenltd.mapper.JavaFXSpringRunner;
import com.evgenltd.mapper.core.TestContextConfig;
import com.evgenltd.mapper.core.TestUtils;
import com.evgenltd.mapper.core.entity.Layer;
import com.evgenltd.mapper.core.entity.Tile;
import com.evgenltd.mapper.core.entity.impl.EntityFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 18-08-2016 23:44
 */
@RunWith(JavaFXSpringRunner.class)
@ContextConfiguration(classes = TestContextConfig.class, loader = AnnotationConfigContextLoader.class)
public class PerformanceTest {

	@Autowired
	@SuppressWarnings("unused")
	private CommonDao commonDao;
	@Autowired
	@SuppressWarnings("unused")
	private LayerBean layerBean;
	@Autowired
	@SuppressWarnings("unused")
	private TileBean tileBean;

	@Test
	public void testGenerateLevels()	{

		final int size = 100;

		// prepare

		final Layer layer = EntityFactory.createLayer();
		commonDao.insert(layer);

		System.out.println("Making test data");

		for(int x=0; x<size; x++)	{
			for(int y=0; y<size; y++)	{
				System.err.println(x+";"+y);
				final Tile tile = TestUtils.createTile(x, y, 1, layer);
				tileBean.update(tile);
			}
		}

		layerBean.generateLevels(
				layer.getId(),
				System.out::println,
				(aLong, aLong2) -> System.err.println("work done " + aLong + " - " + aLong2)
		);

	}

}
