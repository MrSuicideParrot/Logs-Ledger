package pt.up.fc.dcc.ssd.a.kademlia;

class MiddleNode extends RoutingNode{
    private RoutingNode left;
    private RoutingNode right;

    MiddleNode(){
        this.left = new KBucket();
    }

    public RoutingNode addP2PNode(NodeP2P newNode) {
        return null;
    }
}
