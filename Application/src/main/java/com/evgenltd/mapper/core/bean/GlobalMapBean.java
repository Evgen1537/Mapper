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
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.json.*;
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

		final Map<String,Object> parameters = new HashMap<>();
		parameters.put(SESSIONS_ADD_USER_FIELD, user);
		parameters.put(SESSIONS_ADD_SESSION_FIELD, sessionName);
		final String responseBody = request(SESSIONS_ADD_URL, parameters);

		if (responseBody.contains("<html")) {
			return wrapErrorResponse(responseBody);
		}

		return parse(responseBody, json -> json.getString(RESPONSE_GUID_KEY));

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
		final String responseBody = request(SESSIONS_LOCK_URL, Collections.singletonMap(SESSIONS_LOCK_GUID_FIELD, guid));
		return parse(responseBody, this::lockSessionResponseConverter);

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
		final byte[] imageBytes = Utils.imageToByteArray(picture.getImage());
		final ByteArrayBody byteBody = new ByteArrayBody(imageBytes, ContentType.APPLICATION_OCTET_STREAM, "");

		final Map<String,Object> parameters = new HashMap<>();
		parameters.put(TILES_ADD_GUID_FIELD, guid);
		parameters.put(TILES_ADD_X_FIELD, tile.getX());
		parameters.put(TILES_ADD_Y_FIELD, tile.getY());
		parameters.put(TILES_ADD_TILE_FIELD, byteBody);
		final String responseBody = request(TILES_ADD_URL, parameters);

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

	}

	// utils

	private String request(@NotNull final String url, @NotNull final Map<String,Object> parameters) {

		try {

			final HttpRequestWithBody httpRequest = Unirest.post(url);
			MultipartBody body = null;

			for (final Map.Entry<String, Object> entry : parameters.entrySet()) {

				if (body != null) {
					if (entry.getValue() instanceof ByteArrayBody) {
						body = body.field(entry.getKey(), entry.getValue(), true);
					} else {
						body = body.field(entry.getKey(), entry.getValue());
					}
				} else {
					body = httpRequest.field(entry.getKey(), entry.getValue());
				}
			}

			if (body == null) {
				return httpRequest.asString().toString();
			}

			return body.asString().getBody();

		}catch (UnirestException e) {

			throw new GlobalMapException(
					String.format(
							"An exception has occurred on post request, %s",
							Objects.toString(parameters)
					),
					e
			);

		}

	}

	private Object processValue(final Object value) {

		if (value instanceof byte[]) {



		}

		return value;

	}

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
