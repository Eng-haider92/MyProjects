/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 *
 * @author haider-Rajab
 */
public class ClientThread extends Thread{
 
    private Socket socket;
    StringBuilder statusInfo;    
    
    
    @FXML
    private Label connectionStatus;
    
    @FXML
    private Button sendReport;
    
    @FXML 
    private TextField firstName;
    
    @FXML
    private TextField lastName;
    
    @FXML 
    private TextField temperature;
    
    @FXML
    private TextField pressure;
        
    @FXML
    private TextField status;
    
    @FXML
    private URL location;
	
    @FXML
    private ResourceBundle resources;
    
 
   
 
    @FXML
    private void initialize() throws IOException {
        start();
    }
    
    
    public ClientThread() {

    }

    
    
    @Override
    public void run() {
            int port;
            
            try {
                port = Integer.valueOf(Client.getPortNumber());
                socket =  new Socket("localhost",port);
               
                statusInfo = new StringBuilder();
                                           
                Platform.runLater(() -> {
                    statusInfo.append("you are connected now to the Server on port ").append(String.valueOf(port));
                    statusInfo.append("\n");
                    
                    connectionStatus.setText(statusInfo.toString());
                });
               

            } catch (IOException ex) {
                Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    
    @FXML
    public void sendToServer() throws IOException{
            String msgToServer;
            DataInputStream inMsg;
            DataOutputStream outMsg;
            int temp;
         try {
                inMsg = new DataInputStream(socket.getInputStream());                                        
                outMsg = new  DataOutputStream(socket.getOutputStream());
       
                        //send first name 
                        msgToServer = AesEncryption.encrypt(firstName.getText(),Client.getSymmetricKey());
                        outMsg.writeUTF(msgToServer);
                        
                        //send last name
                        msgToServer = AesEncryption.encrypt(lastName.getText(),Client.getSymmetricKey());
                        outMsg.writeUTF(msgToServer);
                        
                        //send temperature
                        msgToServer = AesEncryption.encrypt(temperature.getText(),Client.getSymmetricKey());
                        outMsg.writeUTF(msgToServer);
                        
                        //send pressure 
                        msgToServer = AesEncryption.encrypt(pressure.getText(),Client.getSymmetricKey());
                        outMsg.writeUTF(msgToServer);
                        
                        //send status
                        msgToServer = AesEncryption.encrypt(status.getText(),Client.getSymmetricKey());
                        outMsg.writeUTF(msgToServer);
                        
                        //disable text field after sending information
                        firstName.setStyle("-fx-control-inner-background: khaki;");
                        lastName.setStyle("-fx-control-inner-background: khaki;");
                        temperature.setStyle("-fx-control-inner-background: khaki;");
                        pressure.setStyle("-fx-control-inner-background: khaki;");
                        status.setStyle("-fx-control-inner-background: khaki;");
                        firstName.setEditable(false);
                        lastName.setEditable(false);
                        temperature.setEditable(false);
                        pressure.setEditable(false);
                        status.setEditable(false);
                        
                        Platform.runLater(() -> {
                           statusInfo.append("your information has been sent to the server \n");
                           statusInfo.append("thank you..");
                           connectionStatus.setText(statusInfo.toString());
                        });

                        
                    } catch (IOException ex) {
                        Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
    }

}
