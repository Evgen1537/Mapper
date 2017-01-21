package com.evgenltd.mapper.core.entity.impl;

import com.evgenltd.mapper.core.entity.Picture;
import com.evgenltd.mapper.core.util.Utils;
import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 13-06-2016 01:12
 */
@Entity
@Table(name = "images")
public class PictureImpl implements Picture {

	private static final Logger log = LogManager.getLogger(PictureImpl.class);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private byte[] content;

	@Transient
	private Image image;

	@Override
	public Long getId() {
		return id;
	}
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public byte[] getContent() {
		return content;
	}
	@Override
	public void setContent(byte[] content) {
		this.content = content;
	}

	@Override
	public Image getImage()	{

		if(image == null) {

			try(final InputStream stream = new ByteArrayInputStream(content)) {
				image = new javafx.scene.image.Image(stream);
			}catch(Exception e) {
				log.error("Unable to read image", e);
				throw new RuntimeException(e);
			}

		}

		return image;
	}

	@Override
	public void setImage(Image image)	{
		this.image = image;
		content = Utils.imageToByteArray(image);
	}
}
