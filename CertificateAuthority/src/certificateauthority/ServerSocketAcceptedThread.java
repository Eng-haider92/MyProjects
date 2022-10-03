/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package certificateauthority;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509v2CRLBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CRLConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

/**
 *
 * @author haider-Rajab
 */
public class ServerSocketAcceptedThread extends Thread{
    private final Socket socket;
    private DataInputStream inMsg;
    private DataOutputStream outMsg;
    private final String USERS_LIST  = "C:\\certificate authority\\users.txt";
    private final String CERTIFICATE_PATH  = "C:\\certificate authority";
    private File usersFile;
    private final PublicKey clientPubKey;
    private final PrivateKey privateKey;
    private boolean isFinished;
    X509CRL x509CRL;
    List<X509Certificate> crls  = new ArrayList<>();

       
    public ServerSocketAcceptedThread(Socket s,PublicKey cpk,PrivateKey pvk){
        socket = s;
        clientPubKey= cpk;
        privateKey = pvk; 
        }
        
        
        
    @Override
    public void run() {

        byte[] msg;
        byte[] hashedMsg;
        byte[] signedMsg;
        byte[] decryptedMsg; 
        isFinished = false;
        boolean flag = false;
        String accept = "1";
        String refuse = "0";

        while(!isFinished){
            msg = recieve();
            decryptedMsg = ECIES.decrypt(msg, privateKey);
            hashedMsg= recieve();
            signedMsg= recieve();
            System.out.println(new String(decryptedMsg));
            System.out.println(new String(hashedMsg));
            System.out.println(new String(signedMsg));            
            System.out.println();


            if(ECIES.verifieyMsg(signedMsg, decryptedMsg, clientPubKey)){
                System.out.println("im here"+new String(decryptedMsg));
                if(Arrays.equals(hashedMsg, ECIES.getHashMsg(decryptedMsg))){
                    String msgfromClient = new String(decryptedMsg);
                    System.out.print("im here"+msgfromClient);
                    switch(msgfromClient){
                        case "LOGIN":
                            System.out.print("im here");
                            send(ECIES.encrypt(accept.getBytes(), clientPubKey));
                            send(ECIES.getHashMsg(accept.getBytes()));
                            send(ECIES.signMsg(accept.getBytes(),privateKey));

                            msg = recieve();
                            decryptedMsg = ECIES.decrypt(msg, privateKey);
                            hashedMsg= recieve();
                            signedMsg= recieve();
                            if(ECIES.verifieyMsg(signedMsg,decryptedMsg, clientPubKey)){
                                if(Arrays.equals(hashedMsg, ECIES.getHashMsg(decryptedMsg))){                                    
                                    if(checkMail(new String(decryptedMsg))){
                                        flag = true;
                                        send(ECIES.encrypt(accept.getBytes(), clientPubKey));
                                        send(ECIES.getHashMsg(accept.getBytes()));
                                        send(ECIES.signMsg(accept.getBytes(),privateKey));
                                        X509Certificate cert = X_509Certificate.loadCertificate(CERTIFICATE_PATH, new String(decryptedMsg));
                                        updateTable(cert);
                                    }
                                }
                            }
                            break;
                        case "REGISTER":
                            System.out.println("finally here");
                            send(ECIES.encrypt(accept.getBytes(), clientPubKey));
                            send(ECIES.getHashMsg(accept.getBytes()));
                            send(ECIES.signMsg(accept.getBytes(),privateKey));

                            msg = recieve();
                            decryptedMsg = ECIES.decrypt(msg, privateKey);
                            hashedMsg= recieve();
                            signedMsg= recieve();
                            if(ECIES.verifieyMsg(signedMsg,decryptedMsg, clientPubKey)){
                                if(Arrays.equals(hashedMsg, ECIES.getHashMsg(decryptedMsg))){
                                    System.out.println("ohhhh my god");
                                    if(!checkMail(new String(decryptedMsg))){
                                        flag = true;
                                        System.out.println("woooooooooooooow");
                                        send(ECIES.encrypt(accept.getBytes(), clientPubKey));
                                        send(ECIES.getHashMsg(accept.getBytes()));
                                        send(ECIES.signMsg(accept.getBytes(),privateKey));
                                        updateUserList(new String(decryptedMsg));
                                        registerNewUser(new String(decryptedMsg),clientPubKey,privateKey);
                                    }
                                }
                            }
                            break;
                        case "CERTIFICATE":
                            send(ECIES.encrypt(accept.getBytes(), clientPubKey));
                            send(ECIES.getHashMsg(accept.getBytes()));
                            send(ECIES.signMsg(accept.getBytes(),privateKey));

                            msg = recieve();
                            decryptedMsg = ECIES.decrypt(msg, privateKey);
                            hashedMsg= recieve();
                            signedMsg= recieve();
                            if(ECIES.verifieyMsg(signedMsg,decryptedMsg, clientPubKey)){
                                if(Arrays.equals(hashedMsg, ECIES.getHashMsg(decryptedMsg))){                                   
                                    if(checkMail(new String(decryptedMsg))){
                                        flag = true;
                                        send(ECIES.encrypt(accept.getBytes(), clientPubKey));
                                        send(ECIES.getHashMsg(accept.getBytes()));
                                        send(ECIES.signMsg(accept.getBytes(),privateKey));
                                        Certificate cert = X_509Certificate.loadCertificate(CERTIFICATE_PATH, new String(decryptedMsg));
                                        sendObject(cert);
                                    }
                                }
                            }
                            break;
                        case "REVOKEDLIST":
                            flag = true;
                            send(ECIES.encrypt(accept.getBytes(), clientPubKey));
                            send(ECIES.getHashMsg(accept.getBytes()));
                            send(ECIES.signMsg(accept.getBytes(),privateKey));
                            getCRLs();
                            send(String.valueOf(crls.size()).getBytes());
                            System.out.println(crls.size());                            
                              for(int i =0;i<crls.size();i++){
                                  X509Certificate cc =(X509Certificate) crls.get(i);
                                  sendObject(cc);
                                  System.out.println("ok");
                              }


                            break;
                            case "REVOKED":
                            send(ECIES.encrypt(accept.getBytes(), clientPubKey));
                            send(ECIES.getHashMsg(accept.getBytes()));
                            send(ECIES.signMsg(accept.getBytes(),privateKey));

                            msg = recieve();
                            decryptedMsg = ECIES.decrypt(msg, privateKey);
                            hashedMsg= recieve();
                            signedMsg= recieve();
                            if(ECIES.verifieyMsg(signedMsg,decryptedMsg, clientPubKey)){
                                if(Arrays.equals(hashedMsg, ECIES.getHashMsg(decryptedMsg))){
                                    flag = true;
                                    if(checkMail(new String(decryptedMsg))){
                                        send(ECIES.encrypt(accept.getBytes(), clientPubKey));
                                        send(ECIES.getHashMsg(accept.getBytes()));
                                        send(ECIES.signMsg(accept.getBytes(),privateKey));
                                        X509Certificate cert = X_509Certificate.loadCertificate(CERTIFICATE_PATH, new String(decryptedMsg));
                                        try {
                                            generateCrl(cert);
                                        } catch (Exception ex) {
                                            Logger.getLogger(ServerSocketAcceptedThread.class.getName()).log(Level.SEVERE, null, ex);
                                        }

                                    }
                                    else{
                                        send(ECIES.encrypt(refuse.getBytes(), clientPubKey));
                                        send(ECIES.getHashMsg(refuse.getBytes()));
                                        send(ECIES.signMsg(refuse.getBytes(),privateKey));
                                    }
                                }
                            }
                            break;
                            default:
                                isFinished = true;
                            break;
                    }

                }
            }
            if(!flag){
                send(ECIES.encrypt(refuse.getBytes(), clientPubKey));
                send(ECIES.getHashMsg(refuse.getBytes()));
                send(ECIES.signMsg(refuse.getBytes(),privateKey));
            }
        }
        if(socket!= null)
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(ServerSocketAcceptedThread.class.getName()).log(Level.SEVERE, null, ex);
            }
    }

    private void registerNewUser(String mail,PublicKey pk,PrivateKey pvk){

        X509Certificate cc = X_509Certificate.generateV3Certificate(pk, pvk,mail);
        X_509Certificate.storeCertificate(CERTIFICATE_PATH, cc, mail);
        updateTable(cc);        
    }

    private void updateTable(X509Certificate cc){
        UserCertificate userCertificate = new UserCertificate();
        userCertificate.setEmailAddress(cc.getSubjectDN().getName());
        userCertificate.setStartDate(cc.getNotBefore());
        userCertificate.setExpiredDate(cc.getNotAfter());
        userCertificate.setSN(cc.getSerialNumber().toString());
        userCertificate.setStatus("1");
        
        Main.getServerController().data.add(userCertificate);
        Main.getServerController().tableView.setItems(Main.getServerController().data);
    }
    private boolean checkMail(String mail){
        BufferedReader bf;
        try {
            usersFile = new File(USERS_LIST);
            if(!usersFile.isFile())
                usersFile.createNewFile();
            bf = new BufferedReader(new FileReader(usersFile));
            
          String line;    
          while((line=bf.readLine())!=null)
            {
                if(line.equals(mail))
                    return true;
            }
            bf.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerSocketAcceptedThread.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    private void updateUserList(String mail){

        BufferedWriter bw;
        try {
            usersFile = new File(USERS_LIST);
            bw = new BufferedWriter(new FileWriter(usersFile,true));
            bw.write(mail);
            bw.newLine();
            bw.flush();
            bw.close();

        } catch (IOException ex) {
            Logger.getLogger(ServerSocketAcceptedThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void send(byte[] msg){
        try {               
            outMsg = new DataOutputStream(socket.getOutputStream());
            outMsg.writeInt(msg.length);
            outMsg.write(msg);

        } catch (IOException ex) {
            Logger.getLogger(ServerSocketAcceptedThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendObject(Object obj){

        try {    
            ObjectOutputStream outToClient = new  ObjectOutputStream(socket.getOutputStream());
            outToClient.writeObject(obj);

        } catch (IOException ex) {
            Logger.getLogger(ServerSocketAcceptedThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private byte[] recieve(){
        byte[] msg;
        int length;
        try {
            inMsg = new DataInputStream(socket.getInputStream());
            length= inMsg.readInt();
            msg= new byte[length];
            inMsg.read(msg);
            

            return msg;
        } catch (IOException ex) {
            Logger.getLogger(ServerSocketAcceptedThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private void generateCrl(X509Certificate ca) throws Exception {
        X509v2CRLBuilder builder = new X509v2CRLBuilder(
                new X500Name(ca.getSubjectDN().getName()),
                new Date());
        
    builder.addCRLEntry(ca.getSerialNumber(), new Date(), CRLReason.privilegeWithdrawn);

    JcaContentSignerBuilder contentSignerBuilder = new JcaContentSignerBuilder("SHA256withECDSA");
  
    contentSignerBuilder.setProvider(new BouncyCastleProvider());

    X509CRLHolder crlHolder = builder.build(contentSignerBuilder.build(privateKey));

    JcaX509CRLConverter converter = new JcaX509CRLConverter();
    converter.setProvider(new BouncyCastleProvider());
    
    x509CRL = converter.getCRL(crlHolder);
    
    crls.add(ca);
    System.out.println(x509CRL.toString());
}

    private List<X509Certificate> getCRLs(){
        return crls;
    }
}