package com.evgenltd.mapper.ui.component.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 18-06-2016 19:37
 */
public class CommandScanner {

	private static final Logger log = LogManager.getLogger(CommandManager.class);

	@NotNull
	public static List<Class<?>> scanClasses(@NotNull final String packagePath)	{

		final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		final MetadataReaderFactory factory = new CachingMetadataReaderFactory(resolver);

		final String resolvedSystemOptions = SystemPropertyUtils.resolvePlaceholders(packagePath);
		final String resolvedPackagePath = ClassUtils.convertClassNameToResourcePath(resolvedSystemOptions);
		final String completePackagePath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
				resolvedPackagePath +
				"/" +
				"**/*.class";

		final Resource[] resources = readResources(resolver, completePackagePath);
		final List<Class<?>> commandClasses = new ArrayList<>();

		for(Resource resource : resources) {

			try {

				if(!resource.isReadable())	{
					continue;
				}

				final MetadataReader reader = factory.getMetadataReader(resource);
				final String commandClassName = reader.getClassMetadata().getClassName();

				final Class<?> commandClass = getClassByName(commandClassName);
				commandClasses.add(commandClass);

			}catch(IOException e) {
				log.error("Unable to read class resource=[%s]", resource.getDescription());
			}

		}

		return commandClasses;
	}

	@NotNull
	private static Class<?> getClassByName(@NotNull final String className)	{
		try {
			return Class.forName(className);
		}catch(ClassNotFoundException e) {
			throw new RuntimeException(String.format("Unknown class, name=[%s]",className), e);
		}
	}

	@NotNull
	private static Resource[] readResources(
			@NotNull final ResourcePatternResolver resourceResolver,
			@NotNull final String packagePath
	)	{
		try {
			return resourceResolver.getResources(packagePath);
		}catch(IOException e) {
			throw new RuntimeException(String.format("Unable to read resource, path=[%s]",packagePath),e);
		}
	}
}
