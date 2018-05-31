package app;

import java.io.FileInputStream;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
	
	UIController controller;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader();
		FileInputStream file = new FileInputStream("res/main 0.01.fxml");
		Parent root = loader.load(file);
		controller = loader.getController();
		
		controller.setStage(stage);
		
		stage.setScene(new Scene(root));
		stage.show();
	}
}
