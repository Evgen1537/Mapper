package com.evgenltd.mapper.mapviewer.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 01-07-2016 02:54
 */
public class NodeMergeTest {

	@Test
	public void testGeneric()	{

		List<Node> targetNodeList = new ArrayList<>();
		targetNodeList.add(new Dummy(1L));
		targetNodeList.add(new Dummy(2L));

		final List<Node> sourceNodeList = new ArrayList<>();
		sourceNodeList.add(new Dummy(2L));
		sourceNodeList.add(new Dummy(3L));

		NodeMerge.merge(targetNodeList, sourceNodeList);

		Assert.assertEquals(2,targetNodeList.size(), 0);
		Assert.assertTrue(targetNodeList.contains(new Dummy(2L)));
		Assert.assertTrue(targetNodeList.contains(new Dummy(3L)));
	}

	@Test
	public void testMergeWithInnerState()	{

		List<Node> targetNodeList = new ArrayList<>();
		targetNodeList.add(Dummy.make(1L, true));
		targetNodeList.add(Dummy.make(2L, true));

		final List<Node> sourceNodeList = new ArrayList<>();
		sourceNodeList.add(Dummy.make(2L,false));
		sourceNodeList.add(Dummy.make(3L,false));

		NodeMerge.merge(targetNodeList, sourceNodeList);

		Assert.assertEquals(2,targetNodeList.size(), 0);

		final Dummy dummy2L = (Dummy)targetNodeList.get(0);
		Assert.assertEquals(2L, dummy2L.getIdentifier());
		Assert.assertFalse(dummy2L.isInnerState());

		final Dummy dummy3L = (Dummy)targetNodeList.get(1);
		Assert.assertEquals(3L, dummy3L.getIdentifier());
		Assert.assertFalse(dummy3L.isInnerState());

	}

	@Test
	public void testWithChangedEditState()	{

		List<Node> targetNodeList = new ArrayList<>();
		targetNodeList.add(Dummy.make(1L, true));
		targetNodeList.add(Dummy.make(2L, true, true));
		targetNodeList.add(Dummy.make(3L, true));
		targetNodeList.add(Dummy.make(4L, true, true));

		final List<Node> sourceNodeList = new ArrayList<>();
		sourceNodeList.add(Dummy.make(3L, false));
		sourceNodeList.add(Dummy.make(4L, false));
		sourceNodeList.add(Dummy.make(5L, false));
		sourceNodeList.add(Dummy.make(6L, false));

		NodeMerge.merge(targetNodeList, sourceNodeList);

		List<Dummy> expectedResult = new ArrayList<>();
		expectedResult.add(Dummy.make(2L, true, true));
		expectedResult.add(Dummy.make(3L, false));
		expectedResult.add(Dummy.make(4L, true, true));
		expectedResult.add(Dummy.make(5L, false));
		expectedResult.add(Dummy.make(6L, false));

		Assert.assertEquals(expectedResult.size(), targetNodeList.size());

		for(Node targetNode : targetNodeList) {
			final Dummy targetDummy = (Dummy)targetNode;
			final int expectedNodeIndex = expectedResult.indexOf(targetDummy);
			Assert.assertNotEquals(-1,expectedNodeIndex);

			final Dummy expectedNode = expectedResult.remove(expectedNodeIndex);

			Assert.assertEquals(expectedNode.isInnerState(), targetDummy.isInnerState());
			Assert.assertEquals(expectedNode.isEditing(), targetNode.isEditing());
		}
	}
}