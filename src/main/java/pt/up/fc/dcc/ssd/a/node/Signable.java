package pt.up.fc.dcc.ssd.a.node;

public interface Signable {
    byte[] getDataToSign();
    void setSignature(byte[] signature);
}
