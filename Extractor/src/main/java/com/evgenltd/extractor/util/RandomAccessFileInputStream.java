package com.evgenltd.extractor.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * Project: mapper
 * Author:  Evgeniy Lebedev
 * Created: 24-01-2017 23:20
 */
public class RandomAccessFileInputStream extends InputStream {

	private RandomAccessFile randomAccessFile;

	public RandomAccessFileInputStream(RandomAccessFile randomAccessFile) {
		this.randomAccessFile = randomAccessFile;
	}

	@Override
	public int read() throws IOException {
		return randomAccessFile.read();
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return randomAccessFile.read(b, off, len);
	}

	@Override
	public void close() throws IOException {
		randomAccessFile.close();
	}

}
