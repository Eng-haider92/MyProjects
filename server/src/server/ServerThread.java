/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;


/**
 *
 * @author haider-Rajab
 */
public class ServerThread extends Thread{
    
    
    private int port;    
    StringBuilder sentenses;
    public ObservableList<Report> data;
    
    @FXML
    private Label connectionStatus;
    
    @FXML
    private URL location;
	
    @FXML
    private ResourceBundle resources;
    
            
    @FXML
    public Label test;
    
    @FXML
    public TableView <Report> tableView;
    
    @FXML 
    public TableColumn<Report, Integer> Id;
    
    @FXML 
    public TableColumn<Report, String> firstName;
    
    @FXML 
    public TableColumn<Report, String> lastName;
        
    @FXML 
    public TableColumn<Report, Integer> temperature;
        
    @FXML 
    public TableColumn<Report, String> pressure;
        
    @FXML 
    public TableColumn<Report, String> status;
    

    
 
    @FXML
    private void initialize() throws IOException {
        
        Id.setCellValueFactory(new PropertyValueFactory<>("Id"));
        firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        temperature.setCellValueFactory(new PropertyValueFactory<>("temperature"));
        pressure.setCellValueFactory(new PropertyValueFactory<>("pressure"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        data = FXCollections.observableArrayList();
        
        sentenses.append("Establishing the Server please wait....");
        sentenses.append("\n");
        start();
    }
    
    
    public ServerThread() {
        this.sentenses = new StringBuilder();

    }
    
    
    @Override
    public void run() {
            try {
                Socket socket;
                
                
                port = Integer.valueOf(Server.getPortNumber());
                ServerSocket  serverSocket;
                serverSocket = new ServerSocket(port);
                System.out.println("Server is listening on port "+port+".....\n");
                
                Platform.runLater(() -> {
                    sentenses.append("Server is listening on port ").append(serverSocket.getLocalPort());
                    sentenses.append("\n");
                    connectionStatus.setText(sentenses.toString());
                });

                while (true) {
                    socket = serverSocket.accept();
                    System.out.println("Server is connected to IP"+socket.getInetAddress()+"\n");
                    System.out.println("waiting fo data>>>>>>>>>>>\n");
                    System.out.println("=========================================================\n");
                    sentenses.append("server is connetcted to the host: ").append(socket.getInetAddress().getHostName());
                    sentenses.append("\n");
                    sentenses.append("Host IP is: ").append(socket.getInetAddress());
                    sentenses.append("\n==========================================================\n");

                    Platform.runLater(() -> {

                    connectionStatus.setText(sentenses.toString());
                     });
                    //Start another thread 
                    //to prevent blocked by empty dataInputStream
                    Thread acceptedThread = new Thread(
                        new ServerSocketAcceptedThread(socket));
                    acceptedThread.setDaemon(true); //terminate the thread when program end
                    acceptedThread.start();
                    

                }
            } catch (IOException ex) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }

   
    

}
