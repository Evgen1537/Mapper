package com.evgenltd.mapper.core.bean;

import org.junit.Test;

import java.net.URI;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 25-08-2016 00:24
 */
public class TrackerBeanTest {

	@Test
	public void testReadPositionFile()	{
		try {
			final URI positionFileUri = getClass().getResource("pos.txt").toURI();
			final IntBuffer buffer = FileChannel
					.open(Paths.get(positionFileUri))
					.map(FileChannel.MapMode.READ_ONLY, 0, 8)
					.asIntBuffer();
			System.err.println(buffer.get());
			System.err.println(buffer.get());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
