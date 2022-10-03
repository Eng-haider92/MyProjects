/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package certificateauthority;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 *
 * @author haider-Rajab
 */
public class Main extends Application {
    
    
   
    @FXML
    private Button start;
    
    static private CertificateServer serverController;
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("fxml/FXMLRoot.fxml"));
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Certificate Authority");
        stage.setHeight(550);
        stage.setWidth(800);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    static public CertificateServer getServerController(){
        return Main.serverController;
    }
    
    @FXML
    private void startServer() throws IOException{
        
        //close pervious Stage
        Stage primaryStage = (Stage) start.getScene().getWindow();
        primaryStage.close();
            
        //create new Stage
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/FXMLServerUI.fxml"));
        Parent root = loader.load();            
            
        serverController = loader.getController();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Certificate Authority");
        stage.setHeight(550);
        stage.setWidth(800);
        stage.setResizable(false);
        stage.show();
    }
    
//    @Override
//    public void start(Stage primaryStage) {
//    primaryStage.setOnCloseRequest((WindowEvent e) -> {
//        Platform.exit();
//        System.exit(0);
//    });
//}
}
