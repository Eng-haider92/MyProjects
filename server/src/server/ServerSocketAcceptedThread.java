/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author haider-Rajab
 */
public class ServerSocketAcceptedThread extends Thread{
     
        Socket socket;
        DataInputStream inMsg;
        DataOutputStream outMsg;
        StringBuilder statusInfo;
        static int counter;
        Report report;
        
        public ServerSocketAcceptedThread(Socket s){
            socket = s;

            
    
        }
        
        
        
        @Override
        public void run() {
            
            String msgFromClient;
            String decryptedMsg;
            int length;
            try {               

                report = new Report();
                counter++;
                report.setId(counter);
                
                inMsg = new DataInputStream(socket.getInputStream());
                outMsg = new DataOutputStream(socket.getOutputStream());
  
                
                    msgFromClient = inMsg.readUTF();
                    System.out.println("Encrypted Msg: "+msgFromClient);
                    decryptedMsg = AesEncryption.decrypt(msgFromClient, Server.getSymmetricKey());
                    System.out.println("after dycryprt: "+decryptedMsg);
                    report.setFirstName(decryptedMsg);
                    
                    msgFromClient = inMsg.readUTF();
                    System.out.println("Encrypted Msg: "+msgFromClient);
                    decryptedMsg = AesEncryption.decrypt(msgFromClient, Server.getSymmetricKey());
                    System.out.println("after dycryprt: "+decryptedMsg);
                    report.setLastName(decryptedMsg);
                    
                    msgFromClient = inMsg.readUTF();
                    System.out.println("Encrypted Msg: "+msgFromClient);
                    decryptedMsg = AesEncryption.decrypt(msgFromClient, Server.getSymmetricKey());
                    System.out.println("after dycryprt: "+decryptedMsg);
                    report.setTemperature(decryptedMsg);

                    msgFromClient = inMsg.readUTF();
                    System.out.println("Encrypted Msg: "+msgFromClient);
                    decryptedMsg = AesEncryption.decrypt(msgFromClient, Server.getSymmetricKey());
                    System.out.println("after dycryprt: "+decryptedMsg);
                    report.setPressure(decryptedMsg);
                    

                    
                    msgFromClient = inMsg.readUTF();
                    System.out.println("Encrypted Msg: "+msgFromClient);
                    decryptedMsg = AesEncryption.decrypt(msgFromClient, Server.getSymmetricKey());
                    System.out.println("after dycryprt: "+decryptedMsg);
                    report.setStatus(decryptedMsg);
                    
                System.out.println("data is recieved........");
                
                Server.getMsgController().data.add(report);
                Server.getMsgController().tableView.setItems(Server.getMsgController().data);

                
            } catch (IOException ex) {
                Logger.getLogger(ServerSocketAcceptedThread.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                        
                    } catch (IOException ex) {
                        Logger.getLogger(ServerSocketAcceptedThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                if (inMsg != null) {
                    try {
                        inMsg.close();
                    } catch (IOException ex) {
                        Logger.getLogger(ServerSocketAcceptedThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                if (outMsg != null) {
                    try {
                        outMsg.close();
                    } catch (IOException ex) {
                        Logger.getLogger(ServerSocketAcceptedThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
}