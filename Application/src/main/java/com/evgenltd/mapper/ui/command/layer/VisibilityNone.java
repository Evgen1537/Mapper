package com.evgenltd.mapper.ui.command.layer;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.enums.Visibility;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.component.command.Command;
import com.evgenltd.mapper.ui.component.command.CommandTemplate;
import com.evgenltd.mapper.ui.node.LayerNode;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.event.ActionEvent;

import java.util.List;

/**
 * Project: mapper
 * Author:  Lebedev
 * Created: 07-07-2016 16:05
 */
@CommandTemplate(
        id = UIConstants.VISIBILITY_NONE,
        text = "None",
        longText = "Hide layers",
        graphic = "/image/eye-close.png",
        accelerator = "Shift+N",
        path = "/Layer",
		position = 11
)
public class VisibilityNone extends Command{

    @Override
    protected void execute(final ActionEvent event) {

        final List<Long> selectedLayers = UIContext
                .get()
                .getMapViewerWrapper()
                .getSelectedNodeIds(LayerNode.class);

        if(selectedLayers.isEmpty())    {
            return;
        }

        Context
                .get()
                .getLayerBean()
                .setLayerVisibility(selectedLayers, Visibility.NONE);

        UIContext
                .get()
                .refresh();

    }

}
