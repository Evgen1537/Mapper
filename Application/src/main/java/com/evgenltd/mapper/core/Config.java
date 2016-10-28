package com.evgenltd.mapper.core;

import com.evgenltd.mapper.core.bean.DatabaseBeanMaintenance;
import com.evgenltd.mapper.core.bean.PersistenceInterceptor;
import com.evgenltd.mapper.core.importer.LayerConverter;
import com.evgenltd.mapper.core.importer.LayerOld;
import com.evgenltd.mapper.core.importer.MarkerConverter;
import com.evgenltd.mapper.core.importer.MarkerOld;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.DefaultTransactionStatus;

import javax.persistence.EntityManagerFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 12-06-2016 23:54
 */
@Configuration
@ComponentScan(basePackages = "com.evgenltd.mapper.core")
@EnableTransactionManagement
@EnableScheduling
public class Config {

	public static final String DATABASE_NAME = "data";

	protected String databaseName()	{
		return DATABASE_NAME+".db";
	}

	@Bean
	public ApplicationListener<ContextRefreshedEvent> applicationStartedListener(final DatabaseBeanMaintenance databaseBeanMaintenance)	{
		return contextStartedEvent -> {
			Context
					.get()
					.setSpringContext(contextStartedEvent.getApplicationContext());
			((AbstractApplicationContext)Context.get().getSpringContext()).registerShutdownHook();
			databaseBeanMaintenance.init();
			databaseBeanMaintenance.processMaintenance();
		};
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory()	{

		final Map<String,Object> properties = new HashMap<>();
		properties.put("hibernate.dialect","org.hibernate.dialect.SQLiteDialect");
		properties.put("hibernate.show_sql","false");
		properties.put("hibernate.connection.foreign_keys","true");
		properties.put("hibernate.ejb.interceptor",new PersistenceInterceptor());

		final JpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();

		final LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactory.setDataSource(dataSource());
		entityManagerFactory.setPackagesToScan("com.evgenltd.mapper.core.entity");
		entityManagerFactory.setJpaVendorAdapter(jpaVendorAdapter);
		entityManagerFactory.setJpaPropertyMap(properties);

		return entityManagerFactory;
	}

	@Bean
	public SingleConnectionDataSource dataSource()	{
		try {
			// ugly hack for pragma foreign_keys = on
			// which should be specified on first connection
			Class.forName("org.sqlite.JDBC");
			final Connection connection = DriverManager.getConnection("jdbc:sqlite:"+databaseName());
			connection.setAutoCommit(false);

			final Statement statement = connection.createStatement();
			statement.executeUpdate("pragma foreign_keys = on");
			statement.close();

			final Connection proxy = (Connection) Proxy.newProxyInstance(
					Connection.class.getClassLoader(),
					new Class<?>[]{Connection.class},
					connectionInvocationHandler(connection)
			);

			return new SingleConnectionDataSource(proxy, true);
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Bean
	public PlatformTransactionManager transactionManager(final EntityManagerFactory entityManagerFactory)	{
		final JpaTransactionManager jpaTransactionManager = buildJpaTransactionManager();
		jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);
		return jpaTransactionManager;
	}

	@Bean
	public XStreamMarshaller xStreamMarshaller()    {
		final XStreamMarshaller xStreamMarshaller = new XStreamMarshaller();
		xStreamMarshaller.setAnnotatedClasses(LayerOld.class, MarkerOld.class);
		xStreamMarshaller.setConverters(new LayerConverter(), new MarkerConverter());
		return xStreamMarshaller;
	}

	// hack for hibernate-to-sqlite interaction

	private JpaTransactionManager buildJpaTransactionManager()	{
		return new JpaTransactionManager()	{
			@Override
			protected void doCommit(DefaultTransactionStatus status) {
				commitAllowance.set(true);
				super.doCommit(status);
				commitAllowance.remove();
			}
		};
	}

	private InvocationHandler connectionInvocationHandler(Connection target)	{
		return (proxy, method, args) -> {
			if(method.getName().equals("commit"))	{
				if(commitAllowance.get() == null || !commitAllowance.get()){
					return null;
				}
			}
			return method.invoke(target, args);
		};
	}

	private final ThreadLocal<Boolean> commitAllowance = new ThreadLocal<>();
}
