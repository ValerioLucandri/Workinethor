package logic.workinethor;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

	/**
	 * NavBar that is always over the top of the view
	 */
	private static BorderPane mainLayout;

	@Override
	public void start(Stage stage) throws IOException {

		Stage primaryStage;
		// setup primaryStage
		primaryStage = stage;
		primaryStage.setTitle("WorkineThor");

		// loads NavBar view
		startMainLayout();
		
		// add scene mainLayout to window
		Scene scene = new Scene(mainLayout);
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setResizable(false);

		// load login screen
		mainLayout.setCenter(FXMLLoader.load(Main.class.getResource("view/Login.fxml")));
	}
	
	public static BorderPane getMainLayout() {
		return mainLayout;
	}
	
	//changed for test
	public static boolean startMainLayout() throws IOException {
		mainLayout = FXMLLoader.load(Main.class.getResource("view/NavBar.fxml"));
		if(mainLayout == null)
			return false;
		else return true;
	}
	
	public static void main(String[] args) {
		launch(args);
	}	
}