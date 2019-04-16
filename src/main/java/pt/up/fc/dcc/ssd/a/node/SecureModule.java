package pt.up.fc.dcc.ssd.a.node;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECParameterSpec;

public class SecureModule {
    private KeyPair keys;

    SecureModule(){

    }

    private PrivateKey getKeyFromFile(String keyFilePath) throws IOException, InvalidKeySpecException {
        byte[] keyBytes = Files.readAllBytes(Paths.get(keyFilePath));
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);

        KeyFactory keyFactory = null;

        try{
            keyFactory = KeyFactory.getInstance("ECDSA");
        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
            System.exit(1);
        }
        PrivateKey privKey = keyFactory.generatePrivate(keySpec);
        return privKey;
    }

    X509Certificate getCertFromFile(String certFilePath) throws CertificateException, FileNotFoundException {
        File certFile = new File(certFilePath);
        FileInputStream certFileInputStream = new FileInputStream(certFile);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) cf.generateCertificate(certFileInputStream);
        return cert;
    }

    private void generateKeys()
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException
    {
        ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("B-571");
        KeyPairGenerator g = KeyPairGenerator.getInstance("ECDSA", "BC");
        g.initialize(ecSpec, new SecureRandom());
        keys = g.generateKeyPair();
    }

    /**
     *
     * @param plaintext Plainext to sign
     * @return Signature
     */
    public byte[] sign(byte[] plaintext){
        try {
            Signature ecdsaSign = Signature.getInstance("SHA256withECDSA", "BC");
            ecdsaSign.initSign(this.keys.getPrivate());
            ecdsaSign.update(plaintext);
            return ecdsaSign.sign();
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param plaintext Plaintext
     * @param signature
     * @return
     */
    public boolean verifySign(byte[] plaintext, byte[] signature){
        try {
            Signature ecdsaVerify = Signature.getInstance("SHA256withECDSA", "BC");
            ecdsaVerify.initVerify(this.keys.getPublic());
            ecdsaVerify.update(plaintext);
            return ecdsaVerify.verify(signature);
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }




}
