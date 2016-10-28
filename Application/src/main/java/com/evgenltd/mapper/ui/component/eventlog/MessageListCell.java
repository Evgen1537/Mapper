package com.evgenltd.mapper.ui.component.eventlog;

import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 19-06-2016 17:44
 */
public class MessageListCell extends ListCell<Message> {

	@Override
	protected void updateItem(Message item, boolean empty) {
		super.updateItem(item, empty);
		if(empty || item == null)	{
			setText("");
			setGraphic(null);
		}else {

			setText(String.format(
					"[%s] %s - %s",
					item.getTimestamp(),
					item.getTitle(),
					item.getText()
			));
			if(item.getType().isError())	{
				setGraphicByImage(UIConstants.EXCLAMATION_RED);
			}else if(item.getType().isWarning())	{
				setGraphicByImage(UIConstants.EXCLAMATION);
			}else if(item.getType().isInformation())	{
				setGraphicByImage(UIConstants.INFORMATION_WHITE);
			}

		}
	}

	private void setGraphicByImage(final @NotNull Image image)	{
		final ImageView imageView = new ImageView(image);
		setGraphic(imageView);
	}
}
