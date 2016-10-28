package com.evgenltd.mapper.core.util;

import com.evgenltd.mapper.core.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 22-07-2016 10:40
 */
public class DataBaseBackup {

	private static final Logger log = LogManager.getLogger(DataBaseBackup.class);

	private static final String BACKUP_FOLDER = "backups";
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");

	public static void doBackup()	{

		final File dataBaseFile = new File(Config.DATABASE_NAME+".db");

		if(!dataBaseFile.exists() || !dataBaseFile.isFile())	{
			return;
		}

		final File backupFolder = new File(BACKUP_FOLDER);
		if(!backupFolder.exists())	{
			if(!backupFolder.mkdir())	{
				log.error("Unable to create backup folder");
				return;
			}
		}

		final File dataBaseBackupFile = new File(backupFolder, getNewBackupName());

		copyFile(dataBaseFile, dataBaseBackupFile);

	}

	private static String getNewBackupName()	{

		return Config.DATABASE_NAME + " " + LocalDateTime.now().format(DATE_TIME_FORMATTER) + ".db";

	}

	private static void copyFile(final File source, final File target)	{

		try {

			Files.copy(source.toPath(), target.toPath());

		}catch(IOException ioException) {
			log.error(
					String.format(
							"Backup database failed, source=[%s] target=[%s]",
							source,
							target
					),
					ioException
			);
		}

	}

}
