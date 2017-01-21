package com.evgenltd.mapper.core;

import com.evgenltd.mapper.core.entity.Layer;
import com.evgenltd.mapper.core.entity.Tile;
import com.evgenltd.mapper.core.entity.impl.EntityFactory;
import com.evgenltd.mapper.core.util.Constants;
import com.evgenltd.mapper.core.util.Utils;
import com.evgenltd.mapper.mapviewer.common.ZLevel;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 19-06-2016 18:35
 */
public class TestUtils {

	public static Image drawRandomImage()    {
		BufferedImage image = new BufferedImage(
				Constants.TILE_SIZE,
				Constants.TILE_SIZE,
				2);
		Graphics2D imageGraphic = image.createGraphics();
		for(int i=0; i<Math.random()*50; i++)   {
			int x1 = (int)(Math.random() * Constants.TILE_SIZE);
			int y1 = (int)(Math.random() * Constants.TILE_SIZE);
			int x2 = (int)(Math.random() * Constants.TILE_SIZE);
			int y2 = (int)(Math.random() * Constants.TILE_SIZE);

			int maxColor = 256 * 256 * 256;
			imageGraphic.setPaint(new Color( (int)(Math.random() * maxColor) ));
			imageGraphic.drawLine(x1,y1, x2,y2);
		}
		return SwingFXUtils.toFXImage(image, null);
	}

	public static Tile createTile(double x, double y, int z, Layer layer) {
		final Image image = drawRandomImage();
		final String hash = Utils.calculateHash(image);
		final Tile tile = EntityFactory.createTile();
		tile.setX(x);
		tile.setY(y);
		tile.setZ(ZLevel.valueOf(z));
		tile.setImage(image);
		tile.setHash(hash);
		tile.setLayer(layer);
		return tile;
	}

}
