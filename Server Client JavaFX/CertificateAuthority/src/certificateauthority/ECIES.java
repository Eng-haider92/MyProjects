/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package certificateauthority;


 
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.ECGenParameterSpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
/**
 *
 * @author haider-Rajab
 */
public class ECIES {
    
    
    
    public static void keyGenetrator(String publicKeyPath, String privateKeyPath){
        
        ObjectOutputStream publicOos;
        ObjectOutputStream privateOos;        
        File privateKeyFile;
        File publicKeyFile;        
        final KeyPairGenerator kpg; 
        final KeyPair kyp;
        try {
            Security.addProvider(new BouncyCastleProvider());
            kpg = KeyPairGenerator.getInstance("EC", new BouncyCastleProvider());
           
            kpg.initialize(256);
            kpg.initialize(new ECGenParameterSpec("secp256r1"));            
            kyp = kpg.generateKeyPair();
            
            publicKeyFile = new File(publicKeyPath);
            privateKeyFile = new File(privateKeyPath);
            File parent = publicKeyFile.getParentFile();
            
            if(!parent.exists() && !parent.mkdir()){
                   throw new IllegalStateException("Couldn't create dir: " + parent);
            }else
                parent.mkdirs();
            
            publicOos= new ObjectOutputStream(new FileOutputStream(publicKeyFile));
            privateOos = new ObjectOutputStream(new FileOutputStream(privateKeyFile));
            
            publicOos.writeObject(kyp.getPublic());            
            privateOos.writeObject(kyp.getPrivate());
            
        } catch (NoSuchAlgorithmException | IOException | InvalidAlgorithmParameterException ex) {
            Logger.getLogger(ECIES.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static byte[] encrypt(byte[] text, PublicKey key) {
        byte[] cipherText = null;
        
        try {
            Cipher cipher = Cipher.getInstance("ECIES", new BouncyCastleProvider());
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipherText = cipher.doFinal(text);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
        } catch (IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(ECIES.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cipherText;
    }
        
    public static byte[] decrypt(byte[] text, PrivateKey key) {
        byte[] dectyptedText = null;
        try {
          final Cipher cipher = Cipher.getInstance("ECIES", new BouncyCastleProvider());
          cipher.init(Cipher.DECRYPT_MODE, key);
          dectyptedText = cipher.doFinal(text);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException |
                InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
        }
        return dectyptedText;
    }
        
    
    public static byte[] signMsg(byte[] msg, PrivateKey pvk){
        try {
            Signature ecdsaSign = Signature.getInstance("SHA256withECDSA",new BouncyCastleProvider());
            ecdsaSign.initSign(pvk);
            ecdsaSign.update(msg);
            byte[] signature = ecdsaSign.sign();
            return signature;
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
            Logger.getLogger(ECIES.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
     }
     
    public static boolean verifieyMsg(byte[] signature,byte[] msg, PublicKey pk){                    
        try {
            Signature ecVerify = Signature.getInstance("SHA256withECDSA", new BouncyCastleProvider());
            ecVerify.initVerify(pk);
            ecVerify.update(msg);
            boolean result = ecVerify.verify(signature);
            return result;
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
            Logger.getLogger(ECIES.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
     }
    
    public static byte[] getHashMsg(byte[] stringToHash){
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(stringToHash);
            byte[] hashString = messageDigest.digest();
            return hashString;
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ECIES.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
