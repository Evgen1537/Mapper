package com.evgenltd.mapper.ui.command.marker;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.bean.CommonDao;
import com.evgenltd.mapper.core.entity.Marker;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.component.command.Command;
import com.evgenltd.mapper.ui.component.command.CommandTemplate;
import com.evgenltd.mapper.ui.component.command.scope.MarkerEdit;
import com.evgenltd.mapper.ui.component.eventlog.Message;
import com.evgenltd.mapper.ui.component.mapviewer.MapViewerWrapper;
import com.evgenltd.mapper.ui.component.markerediting.MarkerEditing;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.event.ActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 28-06-2016 11:07
 */
@CommandTemplate(
		id = UIConstants.MARKER_EDIT_APPLY,
		text = "Apply",
		longText = "Apply editing",
		graphic = "/image/tick_32.png",
		accelerator = "Ctrl+Enter",
		path = "/Marker Editing",
		position = 2,
		scope = MarkerEdit.class
)
public class MarkerEditApply extends Command {

	@Override
	protected void execute(ActionEvent event) {

		final MapViewerWrapper mapViewerWrapper = UIContext.get().getMapViewerWrapper();
		mapViewerWrapper.endEditingNode();

		final MarkerEditing markerEditing = UIContext.get().getMarkerEditing();
		final Marker marker = markerEditing.getMarker();
		if(!checkMarker(marker))	{
			return;
		}
		persistMarkerState(marker);
		markerEditing.setMarker(null);

		UIContext.get().getToolbar().hideMarkerEditingTab();
		UIContext.get().getCommandManager().updateHotKeysFromSettings();

		UIContext.get().refresh();

	}

	private boolean checkMarker(@NotNull final Marker marker)	{
		if(marker.getMarkerPointList().isEmpty())	{
			UIContext
					.get()
					.getEventLog()
					.publish(Message
									 .error()
									 .text("Marker should have at least one point")
					);
			return false;
		}
		return true;
	}

	private void persistMarkerState(@NotNull final Marker marker)	{
		final CommonDao commonDao = Context.get().getCommonDao();
		if(marker.getId() == null)	{
			commonDao.insert(marker);
		}else {
			commonDao.update(marker);
		}
	}

}
