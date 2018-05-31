package app;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class EndController implements Initializable {
	
	@FXML
	private Label confirmMessage;
	
	@FXML
	private void closeProgram(ActionEvent event) {
		System.exit(0);
	}
	
	public void addText(String text) {
		confirmMessage.setText(text);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}

}
