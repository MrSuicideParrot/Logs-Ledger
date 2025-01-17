package pt.up.fc.dcc.ssd.a.node;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECParameterSpec;
import pt.up.fc.dcc.ssd.a.Config;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class SecureModule {
    private KeyPair keys;

    SecureModule() {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); // TODO melhorar isto

        try {
            PrivateKey priv = getPrivateKeyFromFile(Config.privKeyFile);
            PublicKey pub = getPublicKeyFromFile(Config.pubKeyFile);
            keys = new KeyPair(pub, priv);
        } catch (Exception e) {
            try {
                generateKeys();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

    }

    private void writeKeyPair() {
        try {

            {
                PublicKey pub = keys.getPublic();
                FileOutputStream out = new FileOutputStream(Config.pubKeyFile);
                out.write(pub.getEncoded());
                out.close();
            }

            {
                PrivateKey priv = keys.getPrivate();
                FileOutputStream out = new FileOutputStream(Config.privKeyFile);
                out.write(priv.getEncoded());
                out.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private PrivateKey getPrivateKeyFromFile(String keyFilePath) throws IOException, InvalidKeySpecException {
        byte[] keyBytes = Files.readAllBytes(Paths.get(keyFilePath));
        return getPrivateKey(keyBytes);
    }

    public static PrivateKey getPrivateKey(byte[] keyBytes) throws InvalidKeySpecException {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);

        KeyFactory keyFactory = null;

        try {
            keyFactory = KeyFactory.getInstance("ECDSA", "BC");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        PrivateKey privKey = keyFactory.generatePrivate(keySpec);
        return privKey;
    }

    private PublicKey getPublicKeyFromFile(String keyFilePath) throws CertificateException, FileNotFoundException, Exception {
        byte[] keyBytes = Files.readAllBytes(Paths.get(keyFilePath));
        return getPublicKey(keyBytes);
    }

    public static PublicKey getPublicKey(byte[] keyBytes) throws InvalidKeySpecException {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

        KeyFactory keyFactory = null;

        try {
            keyFactory = KeyFactory.getInstance("ECDSA", "BC");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        PublicKey pubKey = keyFactory.generatePublic(keySpec);
        return pubKey;
    }

    private void generateKeys()
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("B-571");
        KeyPairGenerator g = KeyPairGenerator.getInstance("ECDSA", "BC");
        g.initialize(ecSpec, new SecureRandom());
        keys = g.generateKeyPair();
        writeKeyPair();
    }

    /**
     * @param plaintext Plainext to sign
     * @return Signature
     */
    public byte[] sign(byte[] plaintext) {
        try {
            Signature ecdsaSign = Signature.getInstance("SHA256withECDSA", "BC");
            ecdsaSign.initSign(this.keys.getPrivate());
            ecdsaSign.update(plaintext);
            return ecdsaSign.sign();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void sign(Signable obj){
        obj.setSignature(this.sign(obj.getDataToSign()));
    }

    /**
     * @param plaintext Plaintext
     * @param signature
     * @return
     */
    public boolean verifySign(byte[] plaintext, byte[] signature) {
        return verifySign(plaintext, signature, keys.getPublic());
    }

    public static boolean verifySign(byte[] plaintext, byte[] signature, PublicKey key) {
        try {
            Signature ecdsaVerify = Signature.getInstance("SHA256withECDSA", "BC");
            ecdsaVerify.initVerify(key);
            ecdsaVerify.update(plaintext);
            return ecdsaVerify.verify(signature);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public byte[] getPubEncoded(){
        return keys.getPublic().getEncoded();
    }


}
