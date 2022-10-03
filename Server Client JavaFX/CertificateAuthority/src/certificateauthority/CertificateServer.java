/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package certificateauthority;

/**
 *
 * @author haider-Rajab
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
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
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


/**
 *
 * @author haider-Rajab
 */
public class CertificateServer extends Thread{
    
    

    final private int CA_PORT_NUMBER = 22335;
    public ObservableList<UserCertificate> data;
    private StringBuilder sentenses;
    private final String PRIVATE_KEY_PATH = "C:\\certificate authority\\private key";
    private final String PUBLIC_KEY_PATH  = "C:\\certificate authority\\public key"; 
    private File publicKeyFile;
    private File privateKeyFile;
    private Socket socket;
    
    @FXML
    private Label logger;
    
    @FXML
    private URL location;
	
    @FXML
    private ResourceBundle resources;
    
            
    @FXML
    public Label info;
    
    
    @FXML
    public TableView <UserCertificate> tableView;
    
    @FXML 
    public TableColumn<UserCertificate, String> SN;
    
    @FXML 
    public TableColumn<UserCertificate, String> emailAddress;
    
    @FXML 
    public TableColumn<UserCertificate, Date> startDate;
        
    @FXML 
    public TableColumn<UserCertificate, Date> expiredDate;
        
    @FXML 
    public TableColumn<UserCertificate, String> status;

    
 
    @FXML
    private void initialize() throws IOException {
        
        SN.setCellValueFactory(new PropertyValueFactory<>("SN"));
        emailAddress.setCellValueFactory(new PropertyValueFactory<>("emailAddress"));
        startDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        expiredDate.setCellValueFactory(new PropertyValueFactory<>("expiredDate"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        data = FXCollections.observableArrayList();
        sentenses = new StringBuilder();
        
        sentenses.append(">>>>>Establishing the Server please wait....");
        sentenses.append("\n");
        start();
        
    }
   
    
    
    @Override
    public void run() {
        
        //int port = Integer.valueOf(Main.getPortNumber());
        ServerSocket  serverSocket;
           try {      
                serverSocket = new ServerSocket(CA_PORT_NUMBER);
                System.out.println("Server is listening on port "+CA_PORT_NUMBER+".....\n");
                
                publicKeyFile = new File(PUBLIC_KEY_PATH);
                privateKeyFile = new File(PRIVATE_KEY_PATH);
                if(!publicKeyFile.exists() && !privateKeyFile.exists())
                    ECIES.keyGenetrator(PUBLIC_KEY_PATH, PRIVATE_KEY_PATH);
                Platform.runLater(() -> {
                    sentenses.append("Server is running on port ").append(serverSocket.getLocalPort());
                    sentenses.append("\n");
                    logger.setText(sentenses.toString());
                });

                while (true) {
                    socket = serverSocket.accept();                   
                    
                    System.out.println(">>>>Server is connected to IP"+socket.getInetAddress()+"\n");
                    System.out.println("waiting fo data>>>>>>>>>>>\n");
                    System.out.println("=========================================================\n");
                    sentenses.append("server is connetcted to the host: ").append(socket.getInetAddress().getHostName());
                    sentenses.append("\n");
                    sentenses.append("Host IP is: ").append(socket.getInetAddress()).append("\n");
                    sentenses.append(">>>>>exchanging keys: \n");                    

                    Platform.runLater(() -> {

                    logger.setText(sentenses.toString());
                     });  
                    
                    exchangeKeys();

                }
            } catch (IOException ex) {
                Logger.getLogger(CertificateServer.class.getName()).log(Level.SEVERE, null, ex);
            }
                          
        Stage currentStage =(Stage) info.getScene().getWindow();
        currentStage.setOnCloseRequest((WindowEvent we) -> {
            try {
                if(socket!= null)
                    socket.close();
            } catch (IOException ex) {
                Logger.getLogger(CertificateServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        });          
        } 
    

    private void exchangeKeys(){
        DataInputStream inMsg;
        DataOutputStream outMsg;
        PublicKey clientPubKey;
        int length;
        byte[] msg;
        byte[] hashedMsg;
        byte[] signedMsg;
        try{
            inMsg = new DataInputStream(socket.getInputStream());                                        
            outMsg = new  DataOutputStream(socket.getOutputStream());
      
            ObjectInputStream publicOis = new ObjectInputStream(new FileInputStream(publicKeyFile));
            ObjectInputStream privateOis = new ObjectInputStream(new FileInputStream(privateKeyFile));
            
            PublicKey publicKey = (PublicKey) publicOis.readObject();
            PrivateKey privateKey = (PrivateKey) privateOis.readObject();
            
            ObjectOutputStream outToClient = new  ObjectOutputStream(socket.getOutputStream());
            outToClient.writeObject(publicKey);
            
            ObjectInputStream inFromClient = new  ObjectInputStream(socket.getInputStream());
            clientPubKey = (PublicKey)inFromClient.readObject();

            //run new thread and handle the request
            sentenses.append("Server public key:").append(publicKey.toString()).append("\n");
            sentenses.append("Client public key: ").append(clientPubKey.toString()).append("\n");
            Platform.runLater(() -> {
                logger.setText(sentenses.toString());
            });
            
            Thread acceptedThread = 
                    new Thread(new ServerSocketAcceptedThread(socket,clientPubKey,privateKey));
            acceptedThread.setDaemon(true); //terminate the thread when program end
            acceptedThread.start();                
            }
        catch (IOException | ClassNotFoundException ex){
                Logger.getLogger(CertificateServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        
    }
}
