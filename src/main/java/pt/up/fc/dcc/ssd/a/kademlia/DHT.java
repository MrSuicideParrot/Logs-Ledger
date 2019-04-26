package pt.up.fc.dcc.ssd.a.kademlia;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class DHT {

    enum IterateType{
        FINDVALUE, STOREVALUE, FINDNODE
    }

    private static RoutingTable rt;
    private Storage st;
    /*
    The most important procedure a Kademlia participant must perform is to
locate the k closest nodes to some given node ID. We call this procedure a node
lookup. Kademlia employs a recursive algorithm for node lookups.  6
     */

    public void nodeLookup(){

    }

    private LinkedList<Node> iterate(byte[] target, IterateType iter){
        Set<Node> networkNode = new HashSet<Node>();

        LinkedList<Node> sl = rt.getClosestContact(target, (HashSet<Node>) networkNode);

        // According to the Kademlia white paper, after a round of FIND_NODE RPCs
        // fails to provide a node closer than closestNode, we should send a
        // FIND_NODE RPC to all remaining nodes in the shortlist that have not
        // yet been contacted.
        boolean queryRest = false;

        if(sl.size() == 0){
            //TODO deu treta
        }

        Node closestNode = sl.peekFirst();

        while(true){

            Set<NodeM> respostas = Collections.synchronizedSet(new HashSet());
            TrakerAsync tracker = new TrakerAsync();

            for(Node i: sl){

                if(networkNode.contains(i))
                    continue;

                switch (iter){
                    case FINDVALUE:
                        respostas.add(i.findValue(target));
                        networkNode.add(i);
                        break;

                    case STOREVALUE:
                    case FINDNODE:
                        respostas.add(i.findNode(target,respostas , tracker));
                        tracker.increment();
                        networkNode.add(i);
                        break;

                     default:
                         System.err.println("Erro");

                }
            }

            while (!tracker.completed());

            /* Processar respostas*/

            for (NodeM i: respostas) {
                if(i != null){
                    Node a = new Node(i);
                    rt.addP2PNode(a);
                    if(!sl.contains(a)){
                        sl.add(a);
                    }
                }
            }

            Collections.sort(sl, new SortNode(target));

            if(closestNode.equals(sl.peekFirst())){
                return sl;
            }
            else{
                closestNode = sl.peekFirst();
            }

        }
    }

    void store(byte[] key, byte[] data){
        st.add(key,data);
    }
}
