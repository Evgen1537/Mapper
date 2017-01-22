package com.evgenltd.mapper.core.util;

import com.evgenltd.mapper.core.entity.Tile;
import com.evgenltd.mapper.mapviewer.common.ZLevel;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import math.geom2d.line.Line2D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.proxy.HibernateProxy;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.util.*;
import java.util.List;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 13-06-2016 03:09
 */
public class Utils {

	private static final Logger log = LogManager.getLogger(Utils.class);

	// size conversation

	public static double toTileSize(final double worldSize)	{
		return BigDecimal
				.valueOf(worldSize / Constants.TILE_SIZE)
				.setScale(0, RoundingMode.UP)
				.doubleValue();
	}

	public static double toTilePosition(final double worldPosition)	{
		return BigDecimal
				.valueOf(worldPosition / Constants.TILE_SIZE)
				.setScale(0, worldPosition < 0 ? RoundingMode.UP : RoundingMode.DOWN)
				.doubleValue();
	}

	@Deprecated
	public static Point2D convertPixelToTile(Point2D pixel) {
		return convertPixelToTile(pixel.getX(), pixel.getY());
	}
	@Deprecated
	public static Point2D convertPixelToTile(double pixelX, double pixelY) {
		return new Point2D(
				convertPixelToTile(pixelX),
				convertPixelToTile(pixelY)
		);
	}
	@Deprecated
	public static double convertPixelToTile(double pixel) {
		return  Utils.adjustNumber(pixel, Constants.TILE_SIZE) / Constants.TILE_SIZE;
	}

	@Deprecated
	public static Point2D convertTileToPixel(Point2D tile)    {
		return convertTileToPixel(tile.getX(), tile.getY());
	}
	@Deprecated
	public static Point2D convertTileToPixel(double tileX, double tileY)    {
		return new Point2D(
				convertTileToPixel(tileX),
				convertTileToPixel(tileY)
		);
	}
	public static double convertTileToPixel(double tile)    {
		return tile * Constants.TILE_SIZE;
	}

	// database data conversation

	public static byte[] imageToByteArray(Image image)  {
		try(ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
			BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
			ImageIO.write(bufferedImage, "png", byteStream);
			return byteStream.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static List<Line2D> splitIntoOutline(String content)   {
		if(content == null || content.equals(""))   {
			return Collections.emptyList();
		}
		String[] items = content.split(";");
		List<Line2D> result = new ArrayList<>(items.length/4);
		for (String item : items) {
			String[] coordinates = item.split(",");
			if(coordinates.length != 4) {
				continue;
			}
			result.add(new Line2D(
					Integer.parseInt(coordinates[0]),
					Integer.parseInt(coordinates[1]),
					Integer.parseInt(coordinates[2]),
					Integer.parseInt(coordinates[3])
			));
		}
		return result;
	}

	public static String joinOutlineToString(List<Line2D> source)    {
		if(source == null || source.isEmpty())  {
			return "";
		}
		StringBuilder result = new StringBuilder();
		for (Line2D value : source) {
			result
					.append((int)value.getX1()).append(",")
					.append((int)value.getY1()).append(",")
					.append((int)value.getX2()).append(",")
					.append((int)value.getY2()).append(";");
		}
		return result.substring(0, result.length() - 1);
	}

	public static math.geom2d.Point2D toPoint(final Object[] row)	{
		if(row.length != 2)	{
			throw new IllegalArgumentException("Column count does not equals 2");
		}

		return new math.geom2d.Point2D(((Double)row[0]).intValue(), ((Double)row[1]).intValue());
	}

	// transition between zoom levels

	public static Point2D adjustPointToLevel(Point2D tilePoint, ZLevel level)  {
		return new Point2D(
				adjustNumber(tilePoint.getX(), level.getMeasure()),
				adjustNumber(tilePoint.getY(), level.getMeasure())
		);
	}

	public static Point2D adjustPointToNextLevel(Point2D tilePoint, ZLevel level)  {
		if(Objects.equals(level, ZLevel.MAX_LEVEL))  {
			throw new IllegalArgumentException(String.format(
					"Level should be lower then %s, current level=[%s]",
					Constants.MAX_LEVEL,
					level
			));
		}
		final ZLevel nextLevel = level.nextLevel();
		return adjustPointToLevel(tilePoint, nextLevel);
	}

	public static double adjustSizeToLevel(double number, int scale)	{
		return adjustNumber(number, scale);
	}

	private static double adjustNumber(double number, int scale)  {
		return number < 0
				? ( ((int)number-scale+1) / scale * scale)
				: ( (int)number / scale * scale );
	}

	// image stitching

	/**
	 * Stitches specified images into one image. Passed map should contains deltas (0;0) (0;1) (1;0) (1;1)
	 * @param tilesForStitching map of delta points and tiles
	 * @return stitched image
	 */
	public static Image stitchImages(Map<Point2D,Tile> tilesForStitching)  {
		// double sized result image
		final BufferedImage rawResult = new BufferedImage(
				2 * Constants.TILE_SIZE,
				2 * Constants.TILE_SIZE,
				2);
		final Graphics2D resultGraphics = rawResult.createGraphics();

		// drawing source fragments on the raw result image
		tilesForStitching.forEach((point, tile) -> {
			final BufferedImage tileImage = SwingFXUtils.fromFXImage(tile.getImage(), null);
			resultGraphics.drawImage(
					tileImage,
					(int)point.getX() * Constants.TILE_SIZE,
					(int)point.getY() * Constants.TILE_SIZE,
					null
			);
		});

		// scaling raw image to Tile.TILE_SIZE
		final BufferedImage result = new BufferedImage(Constants.TILE_SIZE, Constants.TILE_SIZE, 2);
		result.createGraphics().drawImage(rawResult, 0,0, Constants.TILE_SIZE, Constants.TILE_SIZE, null);

		// writing to file and updating cache
		return SwingFXUtils.toFXImage(result, null);
	}

	// hash calculation

	public static String calculateHash(java.util.List<String> hashes)    {
		final int prime = 31;
		int result = 1;
		for (String hash : hashes) {
			result = result + prime * hash.hashCode();
		}
		return calculateHash(String.valueOf(result).getBytes());
	}

	public static String calculateHash(Image image)    {
		byte[] data = imageToByteArray(image);
		return calculateHash(data);
	}

	private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String calculateHash(byte[] data)    {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(data);
			byte[] md5 = messageDigest.digest();
			char[] hexChars = new char[md5.length * 2];
			for ( int j = 0; j < md5.length; j++ ) {
				int v = md5[j] & 0xFF;
				hexChars[j * 2] = hexArray[v >>> 4];
				hexChars[j * 2 + 1] = hexArray[v & 0x0F];
			}
			return new String(hexChars);
		} catch (Exception e) {
			log.error("Unable to calculate hash - returned empty hash",e);
			return "";
		}
	}

	// file check

	public static boolean checkFile(final File file)	{
		return file != null && file.exists() && file.isFile();
	}

	public static boolean checkDirectory(final File directory)	{
		return directory != null && directory.exists() && directory.isDirectory();
	}

	// other

	public static Optional<Point2D> parseTileName(String name)    {

		final String[] parts = name.replace(".png","").split("_");

		if(parts.length!=3) {
			log.warn(String.format("Tile does not math with template - skipped, name=[%s]",name));
			return Optional.empty();
		}

		try {

			return Optional.of(new Point2D(
					Double.parseDouble(parts[1]),
					Double.parseDouble(parts[2])
			));

		}catch(NumberFormatException e) {

			log.error(String.format("Unable to parse tile name - skipped, name=[%s]",name), e);
			return Optional.empty();

		}
	}

	public static String prepareTileName(final Tile tile)	{

		return String.format("tile_%s_%s.png", tile.getX().intValue(), tile.getY().intValue());

	}

	public static void checkInterruption() {
		if(Thread.currentThread().isInterrupted())  {
			throw new RuntimeException("Execution has been interrupted");
		}
	}

	public static boolean isDevelopmentEnvironment()	{
		return Utils.class.getPackage().getImplementationVersion() == null;
	}

	public static Long getId(@Nullable final Object proxy) {

		if (proxy == null) {
			return null;
		}

		if (!(proxy instanceof HibernateProxy)) {
			return null;
		}

		final HibernateProxy hibernateProxy = (HibernateProxy) proxy;
		final Serializable value = hibernateProxy.getHibernateLazyInitializer().getIdentifier();
		if (value instanceof Number) {
			final Number numberValue = (Number) value;
			return numberValue.longValue();
		}

		return null;

	}

}
