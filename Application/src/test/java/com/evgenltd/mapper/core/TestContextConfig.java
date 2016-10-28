package com.evgenltd.mapper.core;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.File;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 11-07-2016 21:50
 */
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "com.magenta.echo.driverpay.core.bean")
public class TestContextConfig extends Config{

	public TestContextConfig() {
		final File db = new File(databaseName());
		if(db.exists() && !db.delete()) {
			throw new RuntimeException("Unable to delete "+databaseName());
		}
	}

	@Override
	protected String databaseName() {
		return "test-data.db";
	}
}
