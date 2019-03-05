package pt.up.fc.dcc.ssd.a.kademlia;

abstract class RoutingNode {
    private int depth;
    private int mask;

    public RoutingNode(int depth, int mask) {
        this.depth = depth;
        this.mask = mask;
    }

    int getDepth() {
        return depth;
    }

    int getMask() {
        return mask;
    }



    abstract RoutingNode addP2PNode(NodeP2P newNode);
}
