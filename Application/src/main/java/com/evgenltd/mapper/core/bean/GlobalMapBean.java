package com.evgenltd.mapper.core.bean;

import com.evgenltd.mapper.core.entity.Picture;
import com.evgenltd.mapper.core.entity.Tile;
import com.evgenltd.mapper.core.entity.dto.GlobalMapResponse;
import com.evgenltd.mapper.core.exception.GlobalMapException;
import com.evgenltd.mapper.core.util.Utils;
import com.evgenltd.mapper.mapviewer.common.ZLevel;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.mashape.unirest.request.body.MultipartBody;
import math.geom2d.Point2D;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.json.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Project: mapper
 * Author:  Evgeniy Lebedev
 * Created: 22-01-2017 23:13
 */
@Component
public class GlobalMapBean extends AbstractBean {

	private static final String MAPPER_NANO_USER_PREFIX = "mapper-nano-";

	private static final String SESSIONS_ADD_URL = "http://odditown.com:8080/haven/sessions/add";
	private static final String SESSIONS_ADD_USER_FIELD = "user";
	private static final String SESSIONS_ADD_SESSION_FIELD = "session";

	private static final String SESSIONS_LOCK_URL = "http://odditown.com:8080/haven/sessions/lock";
	private static final String SESSIONS_LOCK_GUID_FIELD = "guid";

	private static final String TILES_ADD_URL = "http://odditown.com:8080/haven/tiles/add";
	private static final String TILES_ADD_GUID_FIELD = "guid";
	private static final String TILES_ADD_X_FIELD = "x";
	private static final String TILES_ADD_Y_FIELD = "y";
	private static final String TILES_ADD_GRID_ID_FIELD = "grid_id";
	private static final String TILES_ADD_TILE_FIELD = "tile";

	private static final String RESPONSE_MSG_KEY = "msg";
	private static final String RESPONSE_DATA_KEY = "data";
	private static final String RESPONSE_GUID_KEY = "guid";
	private static final String RESPONSE_REASON_KEY = "reason";
	private static final String RESPONSE_MATCH_KEY = "match";
	private static final String RESPONSE_X_KEY = "x";
	private static final String RESPONSE_Y_KEY = "y";

	@Autowired
	private ImageCache imageCache;

	/**
	 * <p>POST</p>
	 * <p><a href="http://odditown.com:8080/haven/sessions/add">http://odditown.com:8080/haven/sessions/add</a></p>
	 * @param sessionName name of session
	 * @return response
	 * @throws GlobalMapException if errors occurred
	 */
	public GlobalMapResponse<String> addSession(@NotNull final String sessionName) throws GlobalMapException {

		Objects.requireNonNull(sessionName);
		final String user = getUser();

		try {

			final String responseBody = Unirest.post(SESSIONS_ADD_URL)
					.field(SESSIONS_ADD_USER_FIELD, user)
					.field(SESSIONS_ADD_SESSION_FIELD, sessionName)
					.asString()
					.getBody();

			if (responseBody.contains("<html")) {
				return wrapErrorResponse(responseBody);
			}

			return parse(responseBody, json -> json.getString(RESPONSE_GUID_KEY));

		}catch (UnirestException e) {
			throw new GlobalMapException("An error has occurred during acquiring GUID", e);
		}

	}

	/**
	 * <p>POST</p>
	 * <p><a href="http://odditown.com:8080/haven/sessions/lock">http://odditown.com:8080/haven/sessions/lock</a></p>
	 * @param guid session guid
	 * @return response
	 * @throws GlobalMapException if errors occurred
	 */
	public GlobalMapResponse<Point2D> lockSession(@NotNull final String guid) throws GlobalMapException {

		Objects.requireNonNull(guid);

		try {
			final String responseBody = Unirest.post(SESSIONS_LOCK_URL)
					.field(SESSIONS_LOCK_GUID_FIELD, guid)
					.asString()
					.getBody();

			return parse(responseBody, this::lockSessionResponseConverter);

		}catch (UnirestException e) {
			throw new GlobalMapException("An error has occurred during acquiring matching result", e);
		}

	}

	private Point2D lockSessionResponseConverter(@NotNull final JsonObject jsonObject) {

		final JsonObject matchBody = jsonObject.getJsonObject(RESPONSE_MATCH_KEY);
		final JsonValue x = matchBody.get(RESPONSE_X_KEY);
		final JsonValue y = matchBody.get(RESPONSE_Y_KEY);
		if (x == JsonValue.NULL || y == JsonValue.NULL) {
			return null;
		}
		final JsonNumber xNumber = (JsonNumber) x;
		final JsonNumber yNumber = (JsonNumber) y;
		return new Point2D(xNumber.doubleValue(), yNumber.doubleValue());

	}

	/**
	 * <p>POST</p>
	 * <p><a href="http://odditown.com:8080/haven/tiles/add">http://odditown.com:8080/haven/tiles/add</a></p>
	 * @param guid session guid
	 * @param tile tile
	 * @return response
	 * @throws GlobalMapException if errors occurred
	 */
	public GlobalMapResponse<String> addTile(@NotNull final String guid, @NotNull final Tile tile) throws GlobalMapException {

		Objects.requireNonNull(guid);
		Objects.requireNonNull(tile);
		if (!Objects.equals(tile.getZ(), ZLevel.Z1)) {
			throw new GlobalMapException(String.format("Supported only first level tiles, passed=[%s]", tile.getZ()));
		}

		// todo hide cache implementation
		final Long imageId = Utils.getId(tile.getImageEntity());
		final Picture picture = imageCache.getImage(imageId);
		final byte[] imageBytes = picture.getContent();
		final ByteArrayBody byteBody = new ByteArrayBody(imageBytes, null);

		try {

			final String responseBody = Unirest.post(TILES_ADD_URL)
					.field(TILES_ADD_GUID_FIELD, guid)
					.field(TILES_ADD_X_FIELD, tile.getX().intValue())
					.field(TILES_ADD_Y_FIELD, tile.getY().intValue())
					.field(TILES_ADD_GRID_ID_FIELD, tile.getGridId())
					.field(TILES_ADD_TILE_FIELD, byteBody, true)
					.asString()
					.getBody();

			if (responseBody.contains("<html")) {
				return wrapErrorResponse(responseBody);
			}

			return parse(responseBody, json -> {
				final JsonValue value = json.get(RESPONSE_REASON_KEY);
				if (value == JsonValue.NULL) {
					return null;
				}
				return value.toString();
			});

		}catch (UnirestException e) {
			throw new GlobalMapException(String.format("An error has occurred during adding tile [%s]", tile.getGridId()), e);
		}

	}

	// utils

	private <T> GlobalMapResponse<T> parse(@NotNull final String responseBody, @NotNull final Function<JsonObject,T> dataConverter) {

		try(final JsonReader reader = Json.createReader(new StringReader(responseBody))) {

			final JsonObject root = reader.readObject();
			final String statusCode = root.getString(RESPONSE_MSG_KEY);
			final JsonObject dataBody = root.getJsonObject(RESPONSE_DATA_KEY);

			final GlobalMapResponse.Status status = GlobalMapResponse.Status.fromCode(statusCode);
			final T data = dataConverter.apply(dataBody);

			final GlobalMapResponse<T> response = new GlobalMapResponse<T>();
			response.setStatus(status);
			response.setData(data);

			return response;

		} catch (final Throwable throwable) {
			throw new GlobalMapException(
					String.format("An exception has occurred on parse response, [%s]", responseBody),
					throwable
			);
		}

	}

	private GlobalMapResponse<String> wrapErrorResponse(@NotNull final String responseBody) {
		final GlobalMapResponse<String> response = new GlobalMapResponse<>();
		response.setStatus(GlobalMapResponse.Status.FAILED);
		response.setData(responseBody);
		return response;
	}

	private String getUser() {

		String version = getClass().getPackage().getImplementationVersion();
		if (version == null) {
			version = "development";
		}
		version = version.replaceAll("\\.", "-");

		return MAPPER_NANO_USER_PREFIX + version;

	}

}
