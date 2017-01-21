package com.evgenltd.mapper.core.util;

import com.evgenltd.mapper.ui.util.RevisionHelper;
import com.rollbar.Rollbar;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 20-07-2016 23:28
 */
public class RollbarWrapper {

	private Rollbar rollbar;

	private UUID uuid;

	public RollbarWrapper() {
		final boolean isDevelopment = Utils.isDevelopmentEnvironment();
		rollbar = new Rollbar("12cfb0af50174a63b33a4ad6c2f9fce2", isDevelopment ? "development" : "production");
	}

	public void error(final Throwable throwable)	{
		if(Utils.isDevelopmentEnvironment())	{
			return;
		}
		rollbar.error(throwable, getAdditionalProperties());
	}

	public void logLaunch()	{

		if(Utils.isDevelopmentEnvironment())	{
			return;
		}

		new Thread(() -> {

			try {

				final byte[] seed = getNetworkInterfaceSeed();
				uuid = UUID.nameUUIDFromBytes(seed);

				rollbar.info("Mapper started", getAdditionalProperties());

			}catch(SocketException ignored) {
				ignored.printStackTrace();
			}

		}).start();

	}

	private Map<String,Object> getAdditionalProperties()	{

		final Map<String,Object> properties = new HashMap<>();

		if(uuid != null) {
			properties.put("UUID", uuid.toString());
		}

		final String version = String.format(
				"%s.%s",
				getClass().getPackage().getImplementationVersion(),
				RevisionHelper.getRevision()
		);
		properties.put("version", version);

		properties.put("memory", formatMemoryStat());

		return properties;
	}

	private byte[] getNetworkInterfaceSeed() throws SocketException {

		final byte[] namedByteArray = new byte[8];

		final Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
		while(networkInterfaceEnumeration.hasMoreElements())	{

			final NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();

			byte[] hardwareAddress = networkInterface.getHardwareAddress();
			if(hardwareAddress == null)	{
				continue;
			}

			try {
				for(int i=0; i<namedByteArray.length; i++)	{
					namedByteArray[i] = (byte)(namedByteArray[i] + hardwareAddress[i]);
				}
			}catch(IndexOutOfBoundsException ignored) {
			}
		}

		return namedByteArray;
	}

	private String formatMemoryStat() {

		final Runtime runtime = Runtime.getRuntime();
		final long maxMemory = runtime.maxMemory() / 1_000_000;
		final long totalMemory = runtime.totalMemory() / 1_000_000;
		final long freeMemory = runtime.freeMemory() / 1_000_000;
		final long usedMemory = totalMemory - freeMemory;

		return String.format(
				"(%s MB / %s MB) max %s MB",
				usedMemory,
				totalMemory,
				maxMemory
		);

	}

}
