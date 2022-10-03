/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package certificateauthority;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStore.Entry;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Date;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v2CRLBuilder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;


/**
 *
 * @author haider-Rajab
 */
public class X_509Certificate {
    

    /**
     *
     * @param publicKey
     * @param privateKey
     * @param owner
     * @return
     */
    public static X509Certificate generateV3Certificate(PublicKey publicKey, PrivateKey privateKey, String owner){
        try {
            Security.addProvider(new BouncyCastleProvider());
            X500NameBuilder xno = new X500NameBuilder();
            xno.addRDN(BCStyle.CN,"CA");
            xno.addRDN(BCStyle.E,owner);
            X500Name certificateOwner = xno.build();            
            X500NameBuilder xni = new X500NameBuilder();
            xni.addRDN(BCStyle.CN,"CA");
            xni.addRDN(BCStyle.E,"housbital");
            X500Name certificateIssuer = xni.build();
            Date startDate = new Date();
            Date expiredDate = new Date(startDate.getTime() + 100 * 86400000l);
            BigInteger sn = new BigInteger(64, new SecureRandom());
            byte[] encoded=publicKey.getEncoded();
            SubjectPublicKeyInfo subPublicKeyInfo = SubjectPublicKeyInfo.getInstance(encoded);
            X509v3CertificateBuilder certBuilder;
            certBuilder = new X509v3CertificateBuilder(certificateIssuer,
                    sn,
                    startDate,
                    expiredDate,
                    certificateOwner,
                    subPublicKeyInfo);            
            ContentSigner sigGen;
            sigGen = new JcaContentSignerBuilder("SHA256withECDSA").setProvider(new BouncyCastleProvider()).build(privateKey);
            X509CertificateHolder certificateHolder = certBuilder.build(sigGen);            
        return new JcaX509CertificateConverter().setProvider(new BouncyCastleProvider()).getCertificate(certificateHolder);
        } catch (OperatorCreationException | CertificateException ex) {
            Logger.getLogger(X_509Certificate.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    
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
            Logger.getLogger(X_509Certificate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void storeCertificate(String path,X509Certificate cert, String clientMail){
        try {
            KeyStore keyStore = KeyStore.getInstance("pkcs12",new BouncyCastleProvider());
            keyStore.load(null,null);
            keyStore.setCertificateEntry(clientMail, cert);
            keyStore.store(new FileOutputStream(new File(path+"\\"+clientMail+".pkc")), "password".toCharArray());
            
        } catch (KeyStoreException | FileNotFoundException ex) {
            Logger.getLogger(X_509Certificate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | NoSuchAlgorithmException | CertificateException ex) {
            Logger.getLogger(X_509Certificate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     

    public static X509Certificate loadCertificate(String path, String clientMail){
        try {
            KeyStore keyStore = KeyStore.getInstance("pkcs12",new BouncyCastleProvider());
            keyStore.load(new FileInputStream(path+"\\"+clientMail+".pkc"),"password".toCharArray());
            X509Certificate cert = (X509Certificate)keyStore.getCertificate(clientMail);
            return cert;
            
        } catch (KeyStoreException | FileNotFoundException ex) {
            Logger.getLogger(X_509Certificate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | NoSuchAlgorithmException | CertificateException ex) {
            Logger.getLogger(X_509Certificate.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static void main(String[] args) throws IOException, ClassNotFoundException, OperatorCreationException, CertificateException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, UnrecoverableEntryException {
        
        
        final String PRIVATE_KEY_PATH = "C:\\Ser\\privateKey";
        final String PUBLIC_KEY_PATH = "C:\\Ser\\publicKey"; 
        
        keyGenetrator(PUBLIC_KEY_PATH, PRIVATE_KEY_PATH);
        File publicKeyFile = new File(PUBLIC_KEY_PATH);
        File privateKeyFile = new File(PRIVATE_KEY_PATH);
        
        ObjectInputStream publicOis = new ObjectInputStream(new FileInputStream(publicKeyFile));
        ObjectInputStream privateOis = new ObjectInputStream(new FileInputStream(privateKeyFile));
            
            PublicKey publicKey = (PublicKey) publicOis.readObject();
            PrivateKey privateKey = (PrivateKey) privateOis.readObject();
        
        
        final String PRIVATE_KEY = "C:\\S\\privateKey";
        final String PUBLIC_KEY= "C:\\S\\publicKey"; 
        
        keyGenetrator(PUBLIC_KEY, PRIVATE_KEY);
        File publicKeyFil = new File(PUBLIC_KEY_PATH);
        File privateKeyFil = new File(PRIVATE_KEY_PATH);
        
        ObjectInputStream publicOi = new ObjectInputStream(new FileInputStream(publicKeyFil));
        ObjectInputStream privateOi = new ObjectInputStream(new FileInputStream(privateKeyFil));
              PublicKey publicK = (PublicKey) publicOi.readObject();
              PrivateKey privateK = (PrivateKey) privateOi.readObject();
        X509Certificate cc = generateV3Certificate(publicKey, privateK, "haider");
        PrivateKeyEntry privateKeyEntry = new PrivateKeyEntry(privateKey,null);
        KeyStore keyStore = KeyStore.getInstance("pkcs12",new BouncyCastleProvider());
        keyStore.load(new FileInputStream("D:\\certifica.p12"),"password".toCharArray());
        
        //keyStore.setCertificateEntry("rsacert", cc);
        keyStore.setEntry("ec-key", privateKeyEntry, new KeyStore.PasswordProtection("pass".toCharArray()));
        //keyStore.setKeyEntry("ECKey", privateK,"password".toCharArray(),null);
        keyStore.store(new FileOutputStream(new File("D:\\certifica.p12")), "password".toCharArray());
        
        
        keyStore.load(new FileInputStream("D:\\certifica.p12"),"password".toCharArray());
        //Certificate cert = (Certificate)keyStore.getCertificate("rsacert");
        Entry key = keyStore.getEntry("ec-key", new KeyStore.PasswordProtection("pass".toCharArray()));
        System.out.println(key.toString());
        
    }
    public void ver(){
//        ContentVerifierProvider contentVerifierProvider = new JcaContentVerifierProviderBuilder()
//    .setProvider("BC").build(pubKey);
//        if (!certHolder.isSignatureValid(contentVerifierProvider))
//        {
//            System.err.println("signature invalid");
//        }
    }
}
