package com.evgenltd.mapper.core.rule;

import com.evgenltd.mapper.core.entity.Layer;
import com.evgenltd.mapper.core.entity.Tile;
import com.evgenltd.mapper.core.entity.impl.EntityFactory;
import com.evgenltd.mapper.core.util.Constants;
import math.geom2d.Point2D;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 27-07-2016 02:28
 */
public class LayerMatcherTest {

	@Test
	public void testOneGroupMatching()	{

		final List<Layer> inputData = Arrays.asList(
				makeLayer(
						1L,
						Constants.ZERO_POINT,
						makeTile(2, 1, "1_2"), makeTile(3, 1, "1_3")
				),
				makeLayer(
						2L,
						Constants.ZERO_POINT,
						makeTile(2, 3, "1_2"),
						makeTile(2, 4, "2")
				),
				makeLayer(
						3L,
						Constants.ZERO_POINT,
						makeTile(5,3,"1_3"), makeTile(6,3,"3")
				)
		);

		final List<LayerMatcher.LayerGroup> groupedMatches = LayerMatcher.matchLayers(
				inputData,
				Collections.emptyList(),
				false,
				this::matchLayers
		);

		Assert.assertEquals(1, groupedMatches.size());
		Assert.assertEquals(3, groupedMatches.get(0).getLayerGroup().size());

		for(final Layer layer : groupedMatches.get(0).getLayerGroup()) {
			final double actualOffsetX = layer.getX();
			final double actualOffsetY = layer.getY();
			if(layer.getId() == 1L)	{
				Assert.assertEquals(0, actualOffsetX, 0);
				Assert.assertEquals(0, actualOffsetY, 0);
			}else if(layer.getId() == 2L)	{
				Assert.assertEquals(0, actualOffsetX, 0);
				Assert.assertEquals(-2, actualOffsetY, 0);
			}else if(layer.getId() == 3L)	{
				Assert.assertEquals(-2, actualOffsetX, 0);
				Assert.assertEquals(-2, actualOffsetY, 0);
			}
		}

	}

	@Test
	public void testMultipleGroupMatching()	{

		final List<Layer> inputData = Arrays.asList(
				makeLayer(
						1L,
						Constants.ZERO_POINT,
						makeTile(2, 1, "1_2"), makeTile(3, 1, "1_3")
				),
				makeLayer(
						2L,
						Constants.ZERO_POINT,
						makeTile(2, 3, "2"),
						makeTile(2, 4, "2")
				),
				makeLayer(
						3L,
						Constants.ZERO_POINT,
						makeTile(5,3,"1_3"), makeTile(6,3,"3")
				)
		);

		final List<LayerMatcher.LayerGroup> groupedMatches = LayerMatcher.matchLayers(
				inputData,
				Collections.emptyList(),
				false,
				this::matchLayers
		);

		Assert.assertEquals(2, groupedMatches.size());
		Assert.assertEquals(2, groupedMatches.get(0).getLayerGroup().size());
		Assert.assertEquals(1, groupedMatches.get(1).getLayerGroup().size());

		for(final Layer layer : groupedMatches.get(0).getLayerGroup()) {
			final double actualOffsetX = layer.getX();
			final double actualOffsetY = layer.getY();
			if(layer.getId() == 1L)	{
				Assert.assertEquals(0, actualOffsetX, 0);
				Assert.assertEquals(0, actualOffsetY, 0);
			}else if(layer.getId() == 3L)	{
				Assert.assertEquals(-2, actualOffsetX, 0);
				Assert.assertEquals(-2, actualOffsetY, 0);
			}
		}

	}

	@Test
	public void testMatchingWithExistsLayers()	{

		final List<Layer> inputData = Arrays.asList(
				makeLayer(
						1L,
						Constants.ZERO_POINT,
						makeTile(2, 1, "1_2"), makeTile(3, 1, "1_3")
				),
				makeLayer(
						2L,
						Constants.ZERO_POINT,
						makeTile(2, 3, "1_2"),
						makeTile(2, 4, "2")
				),
				makeLayer(
						3L,
						Constants.ZERO_POINT,
						makeTile(5,3,"1_3"), makeTile(6,3,"3")
				)
		);

		final Layer existedLayer = makeLayer(
				4L,
				Constants.ZERO_POINT,
				makeTile(5,0,"4"),
				makeTile(5,1,"1_3")
		);

		final List<LayerMatcher.LayerGroup> groupedMatches = LayerMatcher.matchLayers(
				inputData,
				Collections.singletonList(existedLayer),
				false,
				this::matchLayers
		);

		Assert.assertEquals(1, groupedMatches.size());
		Assert.assertEquals(3, groupedMatches.get(0).getLayerGroup().size());

		for(final Layer layer : groupedMatches.get(0).getLayerGroup()) {
			final double actualOffsetX = layer.getX();
			final double actualOffsetY = layer.getY();
			if(layer.getId() == 1L)	{
				Assert.assertEquals(2, actualOffsetX, 0);
				Assert.assertEquals(0, actualOffsetY, 0);
			}else if(layer.getId() == 2L)	{
				Assert.assertEquals(2, actualOffsetX, 0);
				Assert.assertEquals(-2, actualOffsetY, 0);
			}else if(layer.getId() == 3L)	{
				Assert.assertEquals(0, actualOffsetX, 0);
				Assert.assertEquals(-2, actualOffsetY, 0);
			}
		}
	}

	// calculates offset for second layer
	private Optional<Point2D> matchLayers(final Layer first, final Layer second)	{

		for(final Tile firstTile : first.getTileSet()) {
			for(Tile secondTile : second.getTileSet()) {
				if(Objects.equals(firstTile.getHash(), secondTile.getHash()))	{

					final double firstXWithOffset = firstTile.getX() + first.getX();
					final double firstYWithOffset = firstTile.getY() + first.getY();
					final double secondXWithOffset = secondTile.getX() + second.getX();
					final double secondYWithOffset = secondTile.getY() + second.getY();

					return Optional.of(new Point2D(
							firstXWithOffset - secondXWithOffset,
							firstYWithOffset - secondYWithOffset
					));

				}
			}
		}

		return Optional.empty();

	}

	private Layer makeLayer(final Long id, final Point2D offset, final Tile... tiles)	{
		final Layer layer = EntityFactory.createLayer();
		layer.setId(id);
		layer.setX(offset.x());
		layer.setY(offset.y());
		layer.setTileSet(
				Arrays
						.stream(tiles)
						.collect(Collectors.toSet())
		);
		return layer;
	}

	private Tile makeTile(final double x, final double y, final String hash)	{
		final Tile tile = EntityFactory.createTile();
		tile.setX(x);
		tile.setY(y);
		tile.setHash(hash);
		return tile;
	}

}
