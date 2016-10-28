package com.evgenltd.mapper.ui.util;

import java.io.InputStream;
import java.util.jar.Manifest;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 21-07-2016 02:15
 */
public class RevisionHelper {

	public static String getRevision()	{

		try(final InputStream stream = RevisionHelper.class.getResourceAsStream("/META-INF/MANIFEST.MF"))	{
			Manifest manifest = new Manifest(stream);
			return manifest.getMainAttributes().getValue("Revision");
		}catch(Exception exception)	{
			return "";
		}

	}

}
