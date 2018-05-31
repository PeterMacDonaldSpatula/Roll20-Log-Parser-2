package app;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

public class AdvancedOptionsController implements Initializable {
	Stage stage;
	UIController controller;
	
	@FXML
	Label styleSheetPath;
	
	@FXML
	protected void launchCSSSelector() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Log File");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("CSS Files", "*.css"));
		
		File selectedFile = fileChooser.showOpenDialog(stage);
		
		if(selectedFile != null) {
			controller.setCSSFile(selectedFile);
			styleSheetPath.setText(selectedFile.getPath());
		}
	}
	
	@FXML
	protected void closeWindow() {
		stage.close();
	}
	
	public void setCSSPath(String path) {
		styleSheetPath.setText(path);
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	public void setParentController(UIController controller) {
		this.controller = controller;
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}

}
