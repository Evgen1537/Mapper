package com.evgenltd.mapper.core.rule;

import com.evgenltd.mapper.core.TestUtils;
import com.evgenltd.mapper.core.entity.Layer;
import com.evgenltd.mapper.core.entity.Tile;
import com.evgenltd.mapper.core.entity.impl.EntityFactory;
import com.evgenltd.mapper.core.util.Utils;
import com.evgenltd.mapper.mapviewer.common.ZLevel;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 19-06-2016 18:35
 */
public class LayerLevelGenerationTest {

	@Test
	public void mainTest()	{

		final Layer layer = EntityFactory.createLayer();
		final Tile tile1 = TestUtils.createTile(0, 0, 1, layer);
		final Tile tile2 = TestUtils.createTile(0,1,1,layer);
		final Tile tile3 = TestUtils.createTile(9,0,1,layer);
		final Tile tile4 = TestUtils.createTile(9,9,1,layer);
		final Tile tile5 = TestUtils.createTile(0,6,1,layer);
		final Tile tile6 = TestUtils.createTile(-1,0,1,layer);
		final Tile tile7 = TestUtils.createTile(-5, 5, 1, layer);

		final String hash2_1$2 = Utils.calculateHash(Arrays.asList(tile1.getHash(), tile2.getHash()));
		final String hash2_3 = Utils.calculateHash(Collections.singletonList(tile3.getHash()));
		final String hash2_4 = Utils.calculateHash(Collections.singletonList(tile4.getHash()));
		final String hash2_5 = Utils.calculateHash(Collections.singletonList(tile5.getHash()));
		final String hash2_6 = Utils.calculateHash(Collections.singletonList(tile6.getHash()));
		final String hash2_7 = Utils.calculateHash(Collections.singletonList(tile7.getHash()));

		final String hash3_1$2 = Utils.calculateHash(Collections.singletonList(hash2_1$2));
		final String hash3_3 = Utils.calculateHash(Collections.singletonList(hash2_3));
		final String hash3_4 = Utils.calculateHash(Collections.singletonList(hash2_4));
		final String hash3_5 = Utils.calculateHash(Collections.singletonList(hash2_5));
		final String hash3_6 = Utils.calculateHash(Collections.singletonList(hash2_6));
		final String hash3_7 = Utils.calculateHash(Collections.singletonList(hash2_7));

		final String hash4_1$2$5 = Utils.calculateHash(Arrays.asList(hash3_1$2, hash3_5));
		final String hash4_3 = Utils.calculateHash(Collections.singletonList(hash3_3));
		final String hash4_4 = Utils.calculateHash(Collections.singletonList(hash3_4));
		final String hash4_6$7 = Utils.calculateHash(Arrays.asList(hash3_6, hash3_7));

		final String hash5_1$2$3$4$5 = Utils.calculateHash(Arrays.asList(hash4_1$2$5, hash4_3, hash4_4));
		final String hash5_6$7 = Utils.calculateHash(Collections.singletonList(hash4_6$7));

		final List<Tile> result = new ArrayList<>();
		LayerLevelGeneration.execute(
				layer,
				Arrays.asList(
						tile1,
						tile2,
						tile3,
						tile4,
						tile5,
						tile6,
						tile7
				),
				tileInfo -> Optional.empty(),
				result::add,
				msg -> {},
				(aLong, aLong2) -> {}
		);

		final List<String> z2level = result
				.stream()
				.filter(tile -> Objects.equals(tile.getZ(), ZLevel.Z2))
				.map(Tile::getHash)
				.collect(Collectors.toList());
		Assert.assertEquals(6, z2level.size(), 0);
		z2level.remove(hash2_1$2);
		z2level.remove(hash2_3);
		z2level.remove(hash2_4);
		z2level.remove(hash2_5);
		z2level.remove(hash2_6);
		z2level.remove(hash2_7);
		Assert.assertTrue(z2level.isEmpty());

		final List<String> z3level = result
				.stream()
				.filter(tile -> Objects.equals(tile.getZ(), ZLevel.Z3))
				.map(Tile::getHash)
				.collect(Collectors.toList());
		Assert.assertEquals(6,z3level.size(),0);
		z3level.remove(hash3_1$2);
		z3level.remove(hash3_3);
		z3level.remove(hash3_4);
		z3level.remove(hash3_5);
		z3level.remove(hash3_6);
		z3level.remove(hash3_7);
		Assert.assertTrue(z3level.isEmpty());

		final List<String> z4level = result
				.stream()
				.filter(tile -> Objects.equals(tile.getZ(), ZLevel.Z4))
				.map(Tile::getHash)
				.collect(Collectors.toList());
		Assert.assertEquals(4,z4level.size(),0);
		z4level.remove(hash4_1$2$5);
		z4level.remove(hash4_3);
		z4level.remove(hash4_4);
		z4level.remove(hash4_6$7);
		Assert.assertTrue(z4level.isEmpty());

		final List<String> z5level = result
				.stream()
				.filter(tile -> Objects.equals(tile.getZ(), ZLevel.Z5))
				.map(Tile::getHash)
				.collect(Collectors.toList());
		Assert.assertEquals(2,z5level.size(),0);
		z5level.remove(hash5_1$2$3$4$5);
		z5level.remove(hash5_6$7);
		Assert.assertTrue(z5level.isEmpty());
	}

}
