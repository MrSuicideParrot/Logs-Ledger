package pt.up.fc.dcc.ssd.a.kademlia;

import java.util.Comparator;

class SortNode implements Comparator<Node> {
    private byte[] target;

    SortNode(byte[] target){
        this.target = target;
    }

    public int compare(Node a, Node b){ //https://www.geeksforgeeks.org/comparator-interface-java/
        int distA = Node.getDistanceID(target, a.getId());
        int distB = Node.getDistanceID(target, b.getId());

        //TODO Confirmar o que isto dá
        return distA - distB;
    }
}
