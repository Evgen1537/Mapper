package com.evgenltd.mapper.core;

import com.evgenltd.mapper.core.bean.*;
import com.evgenltd.mapper.core.bean.envers.EnversBean;
import com.evgenltd.mapper.core.util.RollbarWrapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 12-06-2016 23:52
 */
public class Context {
	private static Context instance = new Context();

	private ApplicationContext context;

	private Long threadCounter = 0L;
	private final ExecutorService coreExecutor = Executors.newCachedThreadPool(r -> {
		final Thread thread = new Thread(r);
		thread.setName(String.format("CoreThreadPool-%s", ++threadCounter));
		return thread;
	});

	private SettingsBean settingsBean;
	private CommonDao commonDao;
	private Loader loader;
	private TileBean tileBean;
	private LayerBean layerBean;
	private LayerMatcherBean layerMatcherBean;
	private MarkerBean markerBean;
	private TrackerBean trackerBean;
	private CommonBean commonBean;
	private ImporterBean importerBean;
	private EnversBean enversBean;
	private RollbarWrapper rollbarWrapper;
	private ImageCache imageCache;
	private GlobalMapBean globalMapBean;

	// instantiation

	private Context() {}

	public static Context get() {
		return instance;
	}

	public void close() {
		coreExecutor.shutdownNow();
		((ConfigurableApplicationContext) context).close();
	}

	// spring context

	public ApplicationContext getSpringContext() {
		return context;
	}

	public void setSpringContext(ApplicationContext context) {
		this.context = context;
	}

	// executors

	public ExecutorService getCoreExecutor() {
		return coreExecutor;
	}

	public ThreadPoolTaskScheduler getSpringScheduler() {
		return getSpringContext().getBean(ThreadPoolTaskScheduler.class);
	}

	// beans

	public SettingsBean getSettingsBean() {
		if(settingsBean == null)	{
			settingsBean = context.getBean(SettingsBean.class);
		}
		return settingsBean;
	}

	public CommonDao getCommonDao() {
		if(commonDao == null)	{
			commonDao = context.getBean(CommonDao.class);
		}
		return commonDao;
	}

	public Loader getLoader() {
		if(loader == null)	{
			loader = context.getBean(Loader.class);
		}
		return loader;
	}

	public TileBean getTileBean() {
		if(tileBean == null)	{
			tileBean = context.getBean(TileBean.class);
		}
		return tileBean;
	}

	public LayerBean getLayerBean() {
		if(layerBean == null)	{
			layerBean = context.getBean(LayerBean.class);
		}
		return layerBean;
	}

	public LayerMatcherBean getLayerMatcherBean() {
		if(layerMatcherBean == null)	{
			layerMatcherBean = context.getBean(LayerMatcherBean.class);
		}
		return layerMatcherBean;
	}

	public MarkerBean getMarkerBean() {
		if(markerBean == null)	{
			markerBean = context.getBean(MarkerBean.class);
		}
		return markerBean;
	}

	public TrackerBean getTrackerBean() {
		if(trackerBean == null)	{
			trackerBean = context.getBean(TrackerBean.class);
		}
		return trackerBean;
	}

	public CommonBean getCommonBean() {
		if(commonBean == null)  {
			commonBean = context.getBean(CommonBean.class);
		}
		return commonBean;
	}

	public ImporterBean getImporterBean() {
		if(importerBean == null)	{
			importerBean = context.getBean(ImporterBean.class);
		}
		return importerBean;
	}

	public EnversBean getEnversBean() {
		if(enversBean == null)	{
			enversBean = context.getBean(EnversBean.class);
		}
		return enversBean;
	}

	public RollbarWrapper getRollbar() {
		if(rollbarWrapper == null)	{
			rollbarWrapper = new RollbarWrapper();
		}
		return rollbarWrapper;
	}

	public ImageCache getImageCache() {
		if (imageCache == null) {
			imageCache = context.getBean(ImageCache.class);
		}
		return imageCache;
	}

	public GlobalMapBean getGlobalMapBean() {
		if (globalMapBean == null) {
			globalMapBean = context.getBean(GlobalMapBean.class);
		}
		return globalMapBean;
	}
}
