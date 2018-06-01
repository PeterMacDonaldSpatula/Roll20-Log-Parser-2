package app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ResourceBundle;

import org.jsoup.nodes.Document;

import generator.HtmlGenerator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import parser.Log;
import parser.Parser;

public class UIController implements Initializable {
	
	Stage stage;
	
	File selectedFile;
	File outputFile;
	File cssFile;
	
	@FXML
	VBox fileSelectBox;
	
	@FXML
	VBox destSelectionBox;
	
	@FXML
	VBox titleBox;
	
	@FXML
	Label filePath;
	
	@FXML
	Label destinationPath;
	
	@FXML
	TextField titleField;
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	@FXML
	protected void launchFilePicker() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Log File");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("HTML Files", "*.html"));
		
		selectedFile = fileChooser.showOpenDialog(stage);
		
		if(selectedFile != null) {
			filePath.setText(selectedFile.getPath());
		}
	}
	
	@FXML
	protected void launchFileSaver() {
		FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Destination");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Zip Files", "*.zip"));
        outputFile = fileChooser.showSaveDialog(stage);
        
        if(outputFile != null) {
			destinationPath.setText(outputFile.getPath());
		}
	}
	
	@FXML
	protected void startParser() throws IOException {
		boolean success = true;
		boolean madeTempCSS = false;
		if (selectedFile == null || outputFile == null) {
			return;
		}
		
		Parser parser = new Parser();
		
		Document doc = parser.load(selectedFile);
		String title = titleField.getText();
		if (title == null) {
			title = selectedFile.getName().replace(".html", "");
		}
		Log log = parser.parse(doc, titleField.getText());
		
		String outputDir = "output/" + log.getTitle();
		
		if (cssFile == null) {
			InputStream cssData = this.getClass().getResourceAsStream("/app/res/style.css");
			byte[] buffer = new byte[cssData.available()];
			cssData.read(buffer);
			
			madeTempCSS = true;
			
			File targetCssFile = new File("style.css");
			OutputStream outStream = new FileOutputStream(targetCssFile);
			outStream.write(buffer);
			outStream.close();
			
			cssFile = targetCssFile;
		}
		
		HtmlGenerator generator = new HtmlGenerator();
		try {
			generator.generate(log, cssFile.getPath(), selectedFile);
			generator.zip(outputFile, outputDir);
			generator.deleteTempDir(outputDir);
			if (madeTempCSS) {
				File tempCSSFile = new File("style.css");
				tempCSSFile.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
		}
		
		FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/app/res/end 0.01.fxml"));
		Parent root = null;
		try {
			root = loader.load();
		} catch (IOException e) {
			System.out.println("Failed to open end window:");
			e.printStackTrace();
			return;
		}
		
		Stage endWindow = new Stage();
		endWindow.initModality(Modality.WINDOW_MODAL);
		endWindow.initOwner(stage);
		endWindow.setScene(new Scene(root));
		
		EndController endController = loader.getController();
		if (success) {
			endController.addText("File creation successful!");
		} else {
			endController.addText("Something went wrong creating your file! The output file may be missing or incomplete.");
		}
		
		endWindow.show();
	}
	
	@FXML
	protected void openAdvanced() {
		FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/app/res/advanced options 0.01.fxml"));
		Parent root = null;
		try {
			root = loader.load();
		} catch (IOException e) {
			System.out.println("Failed to open options window:");
			e.printStackTrace();
			return;
		}
		
		Stage optionsWindow = new Stage();
		optionsWindow.initModality(Modality.WINDOW_MODAL);
		optionsWindow.initOwner(stage);
		optionsWindow.setScene(new Scene(root));
		
		AdvancedOptionsController newController = loader.getController();
		newController.setParentController(this);
		newController.setStage(optionsWindow);
		if (cssFile != null) {
			newController.setCSSPath(cssFile.getPath());
		}
		
		optionsWindow.show();
	}
	
	public void setCSSFile(File cssFile) {
		this.cssFile = cssFile;
	}

	@Override
	public void initialize(URL url, ResourceBundle res) {
		selectedFile = null;
		outputFile = null;
		cssFile = null;
	}

}
