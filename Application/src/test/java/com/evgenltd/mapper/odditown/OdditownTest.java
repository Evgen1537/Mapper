package com.evgenltd.mapper.odditown;

import com.mashape.unirest.http.Unirest;
import javafx.geometry.Point2D;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Project: mapper</p>
 * <p>Author:  Evgeniy Lebedev</p>
 * <p>Created: 15-02-2017 00:41</p>
 */
public class OdditownTest {

	@Test
	public void similarTest() {

		MapSession mapSession = new MapSession("789");
		mapSession.setPath(new File("E:\\Games\\Haven & Hearth\\amber\\map\\2017-02-12 23.02.29"));
		Uploader uploader = new Uploader();

		boolean process = uploader.ProcessDirectory(mapSession);

		if (process) {

			if (!uploader.postSession(mapSession)) {
				return;
			}

			for (Tile t : mapSession.getTiles()) {
				if (!uploader.postTile(mapSession.getGUID(), t)) {
					//TODO :: Handle Error
					if (uploader.getError().equals("locked"))
						break; //session is locked, not accepting
				}
			}

		}

		uploader.postLockSession(mapSession);
		System.err.println("");

	}

	@Test
	public void test() throws Exception {

		final String result = Unirest.post("http://odditown.com:8080/haven/sessions/add")
				.field("user","mapper-nano-development")
				.field("session", String.valueOf(System.currentTimeMillis()))
				.asString().getBody();

		JSONObject json = new JSONObject(result);
		final String guid = json.getJSONObject("data").getString("guid");

		addTile(guid);

		System.err.println(lockSesion(guid));



	}

	private String addTile(String guid) throws Exception {
		final File directory = new File("E:\\Games\\Haven & Hearth\\amber\\map\\2017-02-12 23.02.29");
		final Map<Point2D,Long> ids = readIds(new File(directory, "ids.txt").toPath());

		for (final String tilePath : directory.list()) {
			if (!tilePath.contains(".png")) {
				continue;
			}
			final File tile = new File(directory, tilePath);
			final String[] parts = tile.getName().replaceAll("\\.png","").split("_");
			final int x = Integer.parseInt(parts[1]);
			final int y = Integer.parseInt(parts[2]);
			final Point2D point = new Point2D(x,y);
			final Long id = ids.get(point);

			final byte[] b = Files.readAllBytes(tile.toPath());
			final ByteArrayBody byteArrayBody = new ByteArrayBody(b, null);

			final String result = Unirest.post("http://odditown.com:8080/haven/tiles/add")
					.field("guid", guid)
					.field("x", x)
					.field("y", y)
					.field("grid_id", id)
					.field("tile", byteArrayBody, true)
					.asString().getBody();
			System.out.println(result);
		}
		return "done";
	}

	private Map<Point2D,Long> readIds(Path path) {
		final Map<Point2D,Long> ids = new HashMap<>();
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
			return ids;
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String lockSesion(String guid) throws Exception {
		return Unirest.post("http://odditown.com:8080/haven/sessions/lock")
				.field("guid", guid)
				.asString().getBody();
	}

}
