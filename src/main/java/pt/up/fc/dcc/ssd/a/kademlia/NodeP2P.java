package pt.up.fc.dcc.ssd.a.kademlia;

class NodeP2P {
    private int ip;
    private int port;
    private byte[] nodeID;

    public NodeP2P(int ip, int port, byte[] nodeID) {
        this.ip = ip;
        this.port = port;
        this.nodeID = nodeID;
    }


    public int getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public byte[] getNodeID() {
        return nodeID;
    }
}
