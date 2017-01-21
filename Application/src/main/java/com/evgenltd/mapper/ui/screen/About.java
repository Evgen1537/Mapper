package com.evgenltd.mapper.ui.screen;

import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.util.RevisionHelper;
import com.evgenltd.mapper.ui.util.UpdateChecker;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 21-07-2016 02:21
 */
public class About extends DialogScreen<Void> {

	@FXML private Label version;
	@FXML private Hyperlink springLink;
	@FXML private Hyperlink hibernateLink;
	@FXML private Hyperlink sqliteLink;
	@FXML private Hyperlink javafxLink;
	@FXML private Hyperlink fugueLink;
	@FXML private Hyperlink versionLink;

	public About() {
		getDialog().getDialogPane().getStylesheets().add("/css/mapper.css");
		version.setText(getClass().getPackage().getImplementationVersion()+"."+ RevisionHelper.getRevision());
		springLink.setOnAction(event -> UIContext.get().getHostServices().showDocument("http://projects.spring.io/spring-framework"));
		hibernateLink.setOnAction(event -> UIContext.get().getHostServices().showDocument("http://hibernate.org/orm"));
		sqliteLink.setOnAction(event -> UIContext.get().getHostServices().showDocument("http://sqlite.org"));
		javafxLink.setOnAction(event -> UIContext.get().getHostServices().showDocument("http://docs.oracle.com/javafx"));
		fugueLink.setOnAction(event -> UIContext.get().getHostServices().showDocument("http://p.yusukekamiyamane.com"));
		updateNewVersionLabel(null, null);
		UpdateChecker.of().check(this::updateNewVersionLabel);
	}

	@Override
	protected String getTitle() {
		return "About";
	}

	@Override
	protected List<ButtonType> getButtonTypes() {
		return Collections.singletonList(ButtonType.CLOSE);
	}

	private void updateNewVersionLabel(@Nullable final UpdateChecker.Version version, @Nullable final String url) {
		versionLink.setVisible(url != null);
		versionLink.setOnAction(event -> UIContext.get().getHostServices().showDocument(url));
	}
}
