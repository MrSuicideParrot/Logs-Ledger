package pt.up.fc.dcc.ssd.a.kademlia;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class DHT {

    enum IterateType{
        FINDVALUE, STOREVALUE, FINDNODE
    }

    private static RoutingTable rt;
    /*
    The most important procedure a Kademlia participant must perform is to
locate the k closest nodes to some given node ID. We call this procedure a node
lookup. Kademlia employs a recursive algorithm for node lookups.  6
     */

    public void nodeLookup(){

    }

    private void iterate(byte[] target, IterateType iter){
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
            for(Node i: sl){

                if(networkNode.contains(i))
                    continue;

                switch (iter){
                    case FINDVALUE:
                        i.findValue();
                        networkNode.add(i);
                        break;

                    case STOREVALUE:
                    case FINDNODE:
                        i.findNode();
                        networkNode.add(i);
                        break;

                     default:
                         System.err.println("Erro");

                }
            }

            /* Processar respostas*/




            /* verificar se temos um novo nó */



        }



/*
for {
		expectedResponses := []*expectedResponse{}
		numExpectedResponses := 0

		// Next we send messages to the first (closest) alpha nodes in the
		// shortlist and wait for a response

		for i, node := range sl.Nodes {
			// Contact only alpha nodes
			if i >= alpha && !queryRest {
				break
			}

			// Don't contact nodes already contacted
			if contacted[string(node.ID)] == true {
				continue
			}

			contacted[string(node.ID)] = true
			query := &message{}
			query.Sender = dht.ht.Self
			query.Receiver = node

			switch t {
			case iterateFindNode:
				query.Type = messageTypeFindNode
				queryData := &queryDataFindNode{}
				queryData.Target = target
				query.Data = queryData
			case iterateFindValue:
				query.Type = messageTypeFindValue
				queryData := &queryDataFindValue{}
				queryData.Target = target
				query.Data = queryData
			case iterateStore:
				query.Type = messageTypeFindNode
				queryData := &queryDataFindNode{}
				queryData.Target = target
				query.Data = queryData
			default:
				panic("Unknown iterate type")
			}

			// Send the async queries and wait for a response
			res, err := dht.networking.sendMessage(query, true, -1)
			if err != nil {
				// Node was unreachable for some reason. We will have to remove
				// it from the shortlist, but we will keep it in our routing
				// table in hopes that it might come back online in the future.
				removeFromShortlist = append(removeFromShortlist, query.Receiver)
				continue
			}

			expectedResponses = append(expectedResponses, res)
		}

		for _, n := range removeFromShortlist {
			sl.RemoveNode(n)
		}

		numExpectedResponses = len(expectedResponses)

		resultChan := make(chan (*message))
		for _, r := range expectedResponses {
			go func(r *expectedResponse) {
				select {
				case result := <-r.ch:
					if result == nil {
						// Channel was closed
						return
					}
					dht.addNode(newNode(result.Sender))
					resultChan <- result
					return
				case <-time.After(dht.options.TMsgTimeout):
					dht.networking.cancelResponse(r)
					return
				}
			}(r)
		}

		var results []*message
		if numExpectedResponses > 0 {
		Loop:
			for {
				select {
				case result := <-resultChan:
					if result != nil {
						results = append(results, result)
					} else {
						numExpectedResponses--
					}
					if len(results) == numExpectedResponses {
						close(resultChan)
						break Loop
					}
				case <-time.After(dht.options.TMsgTimeout):
					close(resultChan)
					break Loop
				}
			}

			for _, result := range results {
				if result.Error != nil {
					sl.RemoveNode(result.Receiver)
					continue
				}
				switch t {
				case iterateFindNode:
					responseData := result.Data.(*responseDataFindNode)
					sl.AppendUniqueNetworkNodes(responseData.Closest)
				case iterateFindValue:
					responseData := result.Data.(*responseDataFindValue)
					// TODO When an iterativeFindValue succeeds, the initiator must
					// store the key/value pair at the closest node seen which did
					// not return the value.
					if responseData.Value != nil {
						return responseData.Value, nil, nil
					}
					sl.AppendUniqueNetworkNodes(responseData.Closest)
				case iterateStore:
					responseData := result.Data.(*responseDataFindNode)
					sl.AppendUniqueNetworkNodes(responseData.Closest)
				}
			}
		}

		if !queryRest && len(sl.Nodes) == 0 {
			return nil, nil, nil
		}

		sort.Sort(sl)

		// If closestNode is unchanged then we are done
		if bytes.Compare(sl.Nodes[0].ID, closestNode.ID) == 0 || queryRest {
			// We are done
			switch t {
			case iterateFindNode:
				if !queryRest {
					queryRest = true
					continue
				}
				return nil, sl.Nodes, nil
			case iterateFindValue:
				return nil, sl.Nodes, nil
			case iterateStore:
				for i, n := range sl.Nodes {
					if i >= k {
						return nil, nil, nil
					}

					query := &message{}
					query.Receiver = n
					query.Sender = dht.ht.Self
					query.Type = messageTypeStore
					queryData := &queryDataStore{}
					queryData.Data = data
					query.Data = queryData
					dht.networking.sendMessage(query, false, -1)
				}
				return nil, nil, nil
			}
		} else {
			closestNode = sl.Nodes[0]
		}
	}

 */

        



    }

    public static void main(String [] args){


    }
}
