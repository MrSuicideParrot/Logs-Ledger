package pt.up.fc.dcc.ssd.a.kademlia;

public class RoutingTable {
    RoutingNode root;

    RoutingTable(){
        root = new KBucket(0,0);
    }

    boolean addP2PNode(NodeP2P newNodeP2P){
        try {
            root = root.addP2PNode(newNodeP2P);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public static int getDistance(byte[] a1, byte[] a2){
        //byte[] dst

    }

}
