package pt.up.fc.dcc.ssd.a.kademlia;

class NodeP2P {
    private int ip;
    private int port;
    private int nodeID;

    public NodeP2P(int ip, int port, int nodeID) {
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

    public int getNodeID() {
        return nodeID;
    }
}
