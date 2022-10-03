/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 *
 * @author haider-Rajab
 */
public class Client extends Application {
    
    @FXML
    private TextField portNumber;       
        
    @FXML
    private TextField symmetricKey;
        
    @FXML
    private URL location;
	
    @FXML
    private ResourceBundle resources;
        
        
        
    @FXML
    private Button start;
       
    static private String port;
    static private String key;
    
     static public String getPortNumber() {
            return Client.port;
        }
        
    static public String getSymmetricKey() {
            return Client.key;
        }
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("fxml/FXMLRoot.fxml"));
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.setTitle("symmetric client");
        stage.setHeight(540);
        stage.setWidth(800);
        stage.show();
    }

    
    @FXML
    public void startClient() throws IOException 
	{
            
            Client.port = portNumber.getText();
            Client.key = symmetricKey.getText();
            //close pervious Stage
            Stage primaryStage = (Stage) start.getScene().getWindow();
            primaryStage.close();
            
            //create new Stage
            Parent root = FXMLLoader .load(getClass().getResource("fxml/FXMLClientUI.fxml")); 
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("symmetric client");
            stage.setHeight(540);
            stage.setWidth(800);
            stage.show();
	}
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
