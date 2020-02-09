package workinethor.view;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import workinethor.Main;

public class MainHomeButtonsController {
	private BorderPane mainLayoutProject = null;

	@FXML
	private void initialize() {
		try {
			mainLayoutProject = FXMLLoader.load(Main.class.getResource("view/MainCreateProjectItems.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//modified for codeSmells
	@FXML
	private void goCreate() throws IOException {
		BorderPane mainLayout = null;
		
		mainLayout = Main.getMainLayout();
		mainLayout.setCenter(mainLayoutProject);
	}

}