package com.evgenltd.mapper.core.bean;

import com.evgenltd.mapper.JavaFXSpringRunner;
import com.evgenltd.mapper.core.TestContextConfig;
import com.evgenltd.mapper.core.TestUtils;
import com.evgenltd.mapper.core.bean.envers.ChangeComparator;
import com.evgenltd.mapper.core.bean.envers.EnversBean;
import com.evgenltd.mapper.core.entity.Layer;
import com.evgenltd.mapper.core.entity.Tile;
import com.evgenltd.mapper.core.entity.envers.*;
import com.evgenltd.mapper.core.entity.impl.EntityFactory;
import com.evgenltd.mapper.core.entity.impl.LayerImpl;
import com.evgenltd.mapper.core.enums.LayerType;
import com.evgenltd.mapper.core.enums.Visibility;
import org.hibernate.envers.RevisionType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 11-07-2016 21:49
 */
@RunWith(JavaFXSpringRunner.class)
@ContextConfiguration(classes = TestContextConfig.class, loader = AnnotationConfigContextLoader.class)
public class EnversTest {

	@Autowired
	@SuppressWarnings("unused")
	private CommonDao commonDao;
	@Autowired
	@SuppressWarnings("unused")
	private EnversBean enversBean;

	@Test
	public void testMainWorkflow()	{

		final Layer layer = EntityFactory.createLayer();
		layer.setName("Test");
		layer.setVisibility(Visibility.FULL);
		layer.setType(LayerType.SESSION);
		commonDao.insert(layer);

		final Long layerId = layer.getId();

		enversBean.undo();

		Assert.assertNull(commonDao.find(LayerImpl.class, layerId));

		final Layer another = EntityFactory.createLayer();
		another.setName("Another");
		another.setVisibility(Visibility.NONE);
		another.setType(LayerType.SESSION);
		commonDao.insert(another);

		final int revinfoSize = commonDao.findNative("select * from REVINFO", Collections.emptyMap()).size();
		Assert.assertEquals(2,revinfoSize);

	}

	@Test
	public void testLayerEnversWorkflow()	{

		final Layer layer = EntityFactory.createLayer();
		layer.setName("Test");
		layer.setType(LayerType.SESSION);
		layer.setVisibility(Visibility.FULL);

		final List<Tile> tileList = Arrays.asList(
				TestUtils.createTile(0,0,1,layer),
				TestUtils.createTile(0,1,1,layer),
				TestUtils.createTile(1,0,1,layer),
				TestUtils.createTile(1,1,1,layer)
		);
		layer.setTileSet(new HashSet<>(tileList));

		commonDao.insert(layer);

		enversBean.undo();

		enversBean.redo();
	}

	@Test
	public void testOrdering()	{

		final List<Aud> source = Arrays.asList(
				createChange(MarkerPointAud.class, RevisionType.ADD),
				createChange(MarkerPointAud.class, RevisionType.MOD),
				createChange(MarkerPointAud.class, RevisionType.DEL),
				createChange(TileAud.class, RevisionType.ADD),
				createChange(TileAud.class, RevisionType.MOD),
				createChange(TileAud.class, RevisionType.DEL),
				createChange(MarkerAud.class, RevisionType.ADD),
				createChange(MarkerAud.class, RevisionType.MOD),
				createChange(MarkerAud.class, RevisionType.DEL),
				createChange(LayerAud.class, RevisionType.ADD),
				createChange(LayerAud.class, RevisionType.MOD),
				createChange(LayerAud.class, RevisionType.DEL)
		);

		source.sort(new ChangeComparator());

		Assert.assertEquals(LayerAud.class, source.get(0).getClass());
		Assert.assertEquals(RevisionType.ADD, source.get(0).getRevType());

		Assert.assertEquals(TileAud.class, source.get(1).getClass());
		Assert.assertEquals(RevisionType.ADD, source.get(1).getRevType());

		Assert.assertEquals(MarkerAud.class, source.get(2).getClass());
		Assert.assertEquals(RevisionType.ADD, source.get(2).getRevType());

		Assert.assertEquals(MarkerPointAud.class, source.get(3).getClass());
		Assert.assertEquals(RevisionType.ADD, source.get(3).getRevType());

		Assert.assertEquals(LayerAud.class, source.get(4).getClass());
		Assert.assertEquals(RevisionType.MOD, source.get(4).getRevType());

		Assert.assertEquals(TileAud.class, source.get(5).getClass());
		Assert.assertEquals(RevisionType.MOD, source.get(5).getRevType());

		Assert.assertEquals(MarkerAud.class, source.get(6).getClass());
		Assert.assertEquals(RevisionType.MOD, source.get(6).getRevType());

		Assert.assertEquals(MarkerPointAud.class, source.get(7).getClass());
		Assert.assertEquals(RevisionType.MOD, source.get(7).getRevType());

		Assert.assertEquals(MarkerPointAud.class, source.get(8).getClass());
		Assert.assertEquals(RevisionType.DEL, source.get(8).getRevType());

		Assert.assertEquals(MarkerAud.class, source.get(9).getClass());
		Assert.assertEquals(RevisionType.DEL, source.get(9).getRevType());

		Assert.assertEquals(TileAud.class, source.get(10).getClass());
		Assert.assertEquals(RevisionType.DEL, source.get(10).getRevType());

		Assert.assertEquals(LayerAud.class, source.get(11).getClass());
		Assert.assertEquals(RevisionType.DEL, source.get(11).getRevType());
	}

	private Aud createChange(final Class<?> changeClass, final RevisionType revisionType)	{
		Aud change;
		if(changeClass.equals(LayerAud.class))	{
			change = new LayerAud();
		}else if(changeClass.equals(TileAud.class)) {
			change = new TileAud();
		}else if(changeClass.equals(MarkerAud.class))	{
			change = new MarkerAud();
		}else {
			change = new MarkerPointAud();
		}
		change.setRevType(revisionType);
		return change;
	}
}
