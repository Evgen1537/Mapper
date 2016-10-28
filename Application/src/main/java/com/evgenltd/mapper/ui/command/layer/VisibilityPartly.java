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
        id = UIConstants.VISIBILITY_PARTLY,
        text = "Partly",
        longText = "Partly show layers",
        graphic = "/image/eye-half.png",
        accelerator = "Shift+P",
        path = "/Layer",
		position = 10
)
public class VisibilityPartly extends Command{

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
                .setLayerVisibility(selectedLayers, Visibility.PARTLY);

        UIContext
                .get()
                .refresh();

    }

}
