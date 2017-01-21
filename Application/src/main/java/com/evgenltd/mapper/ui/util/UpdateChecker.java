package com.evgenltd.mapper.ui.util;

import com.evgenltd.mapper.ui.UIContext;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.action.Action;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Project: mapper
 * Author:  Evgeniy Lebedev
 * Created: 19-01-2017 01:01
 */
public class UpdateChecker {

	private static final Logger log = LogManager.getLogger(UpdateChecker.class);
	private static final String URL = "https://sourceforge.net/projects/hnh-mapper/best_release.json";

	private static final Pattern KEY_VALUE_PATTERN = Pattern.compile("(?<=\"filename\":\\s\").*?(?=\")");
	private static final String FILENAME_KEY = "filename";
	private static final String URL_KEY = "url";

	public static final String UPDATE_CHECKER_ID = "UPDATE_CHECKER_ID";

	private Version availableVersion;
	private String url;

	private UpdateChecker() {}

	public static UpdateChecker of() {
		return new UpdateChecker();
	}

	public void checkWithNotification() {
		check((version, url) -> {
			if (url == null) {
				return;
			}

			Platform.runLater(() -> Notifications
					.create()
					.title("New Version")
					.text(String.format("New version (%s) available.", version))
					.action(new Action("Download", event -> UIContext.get().getHostServices().showDocument(url)))
					.hideAfter(Duration.minutes(1))
					.showInformation());
		});
	}

	public void check(@NotNull final BiConsumer<Version, String> resultCallback) {
		UIContext.get().submit(UpdateChecker.UPDATE_CHECKER_ID, new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				checkImpl();
				return null;
			}

			@Override
			protected void succeeded() {
				resultCallback.accept(availableVersion, url);
			}
		});
	}

	private void checkImpl() {

		final String content = loadPage(URL);
		final String fileName = extractValue(content, FILENAME_KEY);
		if (fileName == null || fileName.length() == 0) {
			return;
		}

		final String availableVersionRaw = fileName.replaceAll("[^\\d\\\\.]","");
		availableVersion = Version.parse(availableVersionRaw);
		final String currentVersionRaw = getClass().getPackage().getImplementationVersion();
		final Version currentVersion = Version.parse(currentVersionRaw);

		if (!availableVersion.isGreaterThan(currentVersion)) {
			return;
		}

		url = extractValue(content, URL_KEY);

		return;

	}

	private static String loadPage(@NotNull final String urlString) {

		Objects.requireNonNull(urlString);

		final URL url = makeURL(urlString);

		try (final InputStream stream = url.openStream();
			 final BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {

			final StringBuilder sb = new StringBuilder();
			String line;
			while( (line = reader.readLine()) != null ) {
				sb.append(line);
			}

			return sb.toString();

		} catch(Exception e) {
			throw new RuntimeException(e);
		}

	}

	private static URL makeURL(@NotNull final String urlString) {
		try {
			return new URL(urlString);
		}catch(MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Nullable
	private static String extractValue(@NotNull final String content, @NotNull final String key) {

		final Matcher matcher = KEY_VALUE_PATTERN.matcher(content);
		if (matcher.find()) {
			return matcher.group();
		}

		return null;

	}

	public static class Version {

		private static final String DELIMITER = "\\.";

		private int major = 0;
		private int minor = 0;
		private int patch = 0;

		private Version() {}

		public static Version parse(@Nullable final String raw) {
			final Version version = new Version();
			if (raw == null) {
				return version;
			}
			final String[] parts = raw.split(DELIMITER);
			version.major = Integer.parseInt(parts[0]);
			version.minor = Integer.parseInt(parts[1]);
			version.patch = Integer.parseInt(parts[2]);
			return version;
		}

		@Override
		public String toString() {
			return String.format("%s.%s.%s", major, minor, patch);
		}

		public boolean isGreaterThan(@NotNull final Version that) {
			if (this.major != that.major) {
				return Integer.compare(this.major, that.major) > 0;
			}

			if (this.minor != that.minor) {
				return Integer.compare(this.minor, that.minor) > 0;
			}

			return Integer.compare(this.patch, that.patch) > 0;
		}
	}
}
