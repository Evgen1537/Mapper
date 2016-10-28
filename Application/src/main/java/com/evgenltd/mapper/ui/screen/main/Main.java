package com.evgenltd.mapper.ui.screen.main;


import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.component.mapviewer.MapViewerWrapper;
import com.evgenltd.mapper.ui.screen.AbstractScreen;
import com.evgenltd.mapper.ui.screen.EventLogBrowser;
import com.evgenltd.mapper.ui.screen.LayerBrowser;
import com.evgenltd.mapper.ui.screen.MarkerBrowser;
import com.evgenltd.mapper.ui.screen.dock.DockPane;
import com.evgenltd.mapper.ui.screen.main.toolbar.Toolbar;
import javafx.scene.layout.BorderPane;
import org.controlsfx.control.StatusBar;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 13-06-2016 02:15
 */
public class Main extends AbstractScreen {

	private final Toolbar toolbar = UIContext.get().getToolbar();
	private final DockPane dockPane = UIContext.get().getDockPane();
	private final StatusBar statusBar = new StatusBar();

	private final TaskManager taskManager = new TaskManager(statusBar);

	private final MapViewerWrapper mapViewerWrapper = UIContext.get().getMapViewerWrapper();

	public Main() {
		initUI();
	}

	private void initUI()	{

		dockPane.dockCenter(mapViewerWrapper.getRoot());
		dockPane.dockLeft(null, "Layer", this::layerBrowserBuilder);
		dockPane.dockRight(null, "Marker", this::markerBrowserBuilder);
		dockPane.dockBottom(null, "Event Log", this::eventLogBuilder, UIContext.get().getEventLog());

		final BorderPane body = new BorderPane();
		body.setTop(toolbar.getRoot());
		body.setCenter(dockPane);
		body.setBottom(statusBar);

		setRoot(body);

	}

	private AbstractScreen layerBrowserBuilder()	{
		return new LayerBrowser();
	}

	private AbstractScreen markerBrowserBuilder()	{
		return new MarkerBrowser();
	}

	private AbstractScreen eventLogBuilder()	{
		return new EventLogBrowser();
	}
}
