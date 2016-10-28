package com.evgenltd.mapper.ui.command.marker;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.ui.component.command.Command;
import com.evgenltd.mapper.ui.component.command.CommandTemplate;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.event.ActionEvent;

/**
 * Project: mapper
 * Author:  Lebedev
 * Created: 07-07-2016 16:05
 */
@CommandTemplate(
        id = UIConstants.MARKER_HIDE,
        text = "Hide markers",
        longText = "Hide/show all markers",
        graphic = "/image/eye-red.png",
        accelerator = "Ctrl+S",
        path = "/Marker",
		position = 5
)
public class MarkerHide extends Command {

    @Override
    protected void execute(final ActionEvent event) {

        Context
                .get()
                .getSettingsBean()
                .setHideMarkers(isSelected());

    }
}
