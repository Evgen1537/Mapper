package com.evgenltd.mapper.mapviewer.demo;

import com.evgenltd.mapper.mapviewer.common.Node;
import com.evgenltd.mapper.mapviewer.common.NodeGroup;
import org.jetbrains.annotations.NotNull;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 03-07-2016 21:49
 */
public class BlockGroup extends NodeGroup	{

	private long id;

	public BlockGroup(long id) {
		this.id = id;
	}

	@Override
	public long getIdentifier() {
		return id;
	}

	@Override
	protected void merge(@NotNull Node source) {}

	public BlockGroup copy()	{
		return copy(id);
	}

	public BlockGroup copy(long id)  {
		final BlockGroup newBlockGroup = new BlockGroup(id);
		for(final Node node : getChildren()) {
			final Block block = (Block)node;
			newBlockGroup.add(block.copy());
		}
		return newBlockGroup;
	}

	@Override
	public String toString() {
		return "BlockGroup{" +
				"id=" + id +
				'}';
	}
}
