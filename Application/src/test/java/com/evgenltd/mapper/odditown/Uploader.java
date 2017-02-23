package com.evgenltd.mapper.odditown;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.geometry.Point2D;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * <p>Project: mapper</p>
 * <p>Author:  Evgeniy Lebedev</p>
 * <p>Created: 15-02-2017 01:10</p>
 */
public class Uploader {

	public static final String SESSION_URL = "http://odditown.com:8080/haven/sessions/add";
	public static final String SESSION_LOCK_URL = "http://odditown.com:8080/haven/sessions/lock";
	public static final String TILE_URL = "http://odditown.com:8080/haven/tiles/add";

	final Color c = new Color(0, 0, 0);
	final Color c2 = new Color(157, 154, 151);
	final Color c3 = new Color(148, 145, 141);

	private String error;
	private Map<Point2D,Long> ids = new HashMap<>();

	public String getError() {
		return error;
	}

	private void readIds(Path path) {
		try {
			final List<String> lines = Files.readAllLines(path);
			for (String line : lines) {
				final String[] parts = line.split(",");
				final Point2D point = new Point2D(
						Integer.parseInt(parts[0]),
						Integer.parseInt(parts[1])
				);
				final Long id = Long.parseLong(parts[2]);
				ids.put(point, id);
			}
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public boolean ProcessDirectory(MapSession mapSession) {
		File f = mapSession.getPath();

		readIds(new File(f,"ids.txt").toPath());

		File[] tiles = f.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File directory, String fileName) {
				return fileName.matches("tile_(-?)(\\d*)_(-?)(\\d*).png");
			}
		});

		if (tiles == null) {
			return false;
		}

		if (tiles.length < 10) {
			return false; //No point in sending small sessions
		}
		//Check if I really want the session
		if (!precheckSession(tiles)) {
			return false;
		}

		for (File tileFile : tiles) {

			String[] coords = tileFile.getName().substring(0, tileFile.getName().length() - 4).split("_");

			//create new Tile object
			int x;
			int y;
			try {
				x = Integer.parseInt(coords[1]);
				y = Integer.parseInt(coords[2]);
			} catch (NumberFormatException nfe) {
				continue;
			}

			final Long id = ids.get(new Point2D(x,y));

			Tile temp = new Tile(x, y, tileFile, id);

			//add it to session
			mapSession.addTile(temp);
		}

		return true;
	}

	public boolean precheckSession(File[] tiles) {

		for (File tile : tiles) {
			try {
				BufferedImage img = ImageIO.read(tile);

				int possibleBlack = img.getHeight() * img.getWidth();
				double black = 0;
				for (int i = 0; i < img.getWidth(); i++) {
					for (int j = 0; j < img.getHeight(); j++) {
						int imgRGB = img.getRGB(i, j);

						if (imgRGB == c2.getRGB() || imgRGB == c3.getRGB()) {
							return false;
						}

						if (imgRGB == c.getRGB()) {
							black++;
						}
					}
				}

				if ((((black) / possibleBlack) * 100) > 90) {
					return false;
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		return true;
	}


	public boolean postSession(MapSession session) {
		error = "";
		try {
			HttpResponse response = Unirest.post(SESSION_URL)
					.field("user", "mapper-nano-development") //MU- Just to tell me the source of the upload
					.field("session", session.getName())
					.asString();

			JSONObject json = new JSONObject(response.getBody().toString());

			if (json.getString("msg").equals("success")) {
				session.setGUID(json.getJSONObject("data").getString("guid"));
				return true;
			} else {
				if (json.getString("msg").equals("failed")) {
					if (json.getJSONObject("data").getString("reason").contains("locked")) {
						error = "locked";
					}
				}
				return false;
			}
		} catch (UnirestException e) {
			error = "UnirestError";
		}

		return false;
	}

	public boolean postTile(String guid, Tile t) {
		try {

			final File tile = t.getTile();
			String[] coords = tile.getName().substring(0, tile.getName().length() - 4).split("_");

			HttpResponse response = Unirest.post(TILE_URL)
					.field("guid", guid)
					.field("x", coords[1])
					.field("y", coords[2])
					.field("grid_id", t.getId())
					.field("tile", tile)
					.asString();

			JSONObject jsonObject = new JSONObject(response.getBody().toString());

			if (jsonObject.getString("msg").equals("success")) {
				return true;
			} else {
				System.out.println("\t\tFailed to post tile :: " + jsonObject.getJSONObject("data").getString("reason"));
				error = jsonObject.getJSONObject("data").getString("reason");
				return false;
			}
		} catch (UnirestException e) {
			error = "UnirestError";
		}
		return false;
	}

	public boolean postLockSession(MapSession mapSession) {
		error = "";
		try {
			HttpResponse response = Unirest.post(SESSION_LOCK_URL)
					.field("guid", mapSession.getGUID())
					.asString();

			//System.out.println(response.getBody().toString());
			JSONObject jsonObject = new JSONObject(response.getBody().toString());

			if (jsonObject.getString("msg").equals("success")) {
				int xOffset = jsonObject.getJSONObject("data").getJSONObject("match").getInt("x");
				int yOffset = jsonObject.getJSONObject("data").getJSONObject("match").getInt("y");
				//System.out.println("Session Offset :: {" + xOffset + "," + yOffset + "}");
				mapSession.setMatched(true);
				mapSession.setXOffset(xOffset);
				mapSession.setYOffset(yOffset);
				return true;
			} else {
				//Failed to match
				//System.out.println("Session did not match.");
				mapSession.setMatched(false);
				return true;
			}
		} catch (UnirestException e) {
			error = "UnirestError";
		}
		return false;
	}
}
