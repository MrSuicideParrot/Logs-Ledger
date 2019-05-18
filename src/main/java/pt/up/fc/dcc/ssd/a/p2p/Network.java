package pt.up.fc.dcc.ssd.a.p2p;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.up.fc.dcc.ssd.a.Config;
import pt.up.fc.dcc.ssd.a.blockchain.*;
import pt.up.fc.dcc.ssd.a.node.SecureModule;
import pt.up.fc.dcc.ssd.a.utils.ArrayTools;
import pt.up.fc.dcc.ssd.a.utils.CriptoTools;

import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class Network {

    HashMap<ByteString, Node> nodes;
    ConfidenceBuckets conf;
    Hello myHello;
    SecureModule sec;
    BlockChain blockChain;

    Lock lock;
    private static final Logger logger = Logger.getLogger(Network.class.getName());


    public Network(SecureModule sec, BlockChain blockChain){
        conf = new ConfidenceBuckets();
        this.sec = sec;
        lock = new ReentrantLock();
        this.blockChain = blockChain;
        // Generate hello
       {
            Hello.Builder buildHello = Hello.newBuilder();
            Hello.HelloContent.Builder contentBuild =  buildHello.getHelloBuilder();
            contentBuild.setPublicKey(ByteString.copyFrom(sec.getPubEncoded()));
            contentBuild.setNodeID(Config.myID);
            contentBuild.setIpv4(Config.ipv4);
            Hello.HelloContent con = contentBuild.build();
            byte[] assin = sec.sign(con.toByteArray());
            buildHello.setAssin(ByteString.copyFrom(assin));
            myHello = buildHello.build();
        }

        nodes = new HashMap<>();
    }

    public void gossipLog(LogType log){
        //logger.info("Gossip do log iniciado: " + ArrayTools.bytesToHex(CriptoTools.hash(log.toByteArray())));

        LogGossip.Builder builder = LogGossip.newBuilder();
        builder.setLog(log);
        builder.setNodeID(Config.myID);
        builder.setAssin(ByteString.copyFrom(sec.sign(log.toByteArray())));
        LogGossip pLog = builder.build();

        for(Node i : conf){
            i.newLog(pLog);
        }
    }

    public void gossipBlock(BlockType block){
        //logger.info("Gossip do bloco iniciado: "+  ArrayTools.bytesToHex(CriptoTools.hash(block.toByteArray())));

        BlockGossip.Builder builder = BlockGossip.newBuilder();
        builder.setBlock(block);
        builder.setNodeID(Config.myID);
        builder.setAssin(ByteString.copyFrom(sec.sign(block.toByteArray())));
        //builder.set
        BlockGossip pBlock = builder.build();

        for(Node i : conf){
            i.newBlock(pBlock);
        }
    }

    public boolean verifyNewBlock(BlockType newBlock){
        // TODO create verify block
        return false;
    }

    public void initializeNodes(HashMap<byte[], String> peers){
        for (String i:
             peers.values()) {
            firstContactNode(i);
        }
    }

    public Hello getHello() {
        return myHello;
    }

    public void addP2P(Hello h){
        try {
            Hello.HelloContent con = h.getHello();

            if(!nodes.containsKey(con.getNodeID().toByteArray())){
                PublicKey pubKey = SecureModule.getPublicKey(con.getPublicKey().toByteArray());
                if(SecureModule.verifySign(con.toByteArray(),h.getAssin().toByteArray(), pubKey)){
                    PublicKey candCert = SecureModule.getPublicKey(con.getPublicKey().toByteArray());
                    if(SecureModule.verifySign(h.getHello().toByteArray(), h.getAssin().toByteArray(), candCert)){
                        Node no = new Node(con.getNodeID(),con.getIpv4(),pubKey, this);
                        logger.info("New node created " + con.getIpv4());
                        lock.lock();
                        nodes.put(no.getId(), no);
                        conf.addP2PNode(no);
                        lock.unlock();
                    }
                }
            }
        }
        catch (Exception e){
            logger.warning(e.toString());
        }


    }

    public void firstContactNode(String ip){
        ManagedChannelBuilder chanelBuilder = ManagedChannelBuilder.forAddress(ip, Config.port_node);
        chanelBuilder.usePlaintext();

        ManagedChannel chanel = chanelBuilder.build();
        BlockChainServiceGrpc.BlockChainServiceStub asyncStub = BlockChainServiceGrpc.newStub(chanel);
        asyncStub.helloNode(myHello, new HelloObserver(this, chanel ));
    }

    public List<Node> getConfidenceNodes(){
        return conf.getConfidenceNodes();
    }

    public List<Node> getRandomNodes(int n){
        return getRandomNodes(n, null);
    }

    public List<Node> getRandomNodes(int n,List<Node> knownN){
        List<Node> l =  new LinkedList<>(nodes.values());
        return getRandomNodes(n, l, knownN);
    }

    public List<Node> getRandomConfNodes(int n){
        return getRandomConfNodes(n, null);
    }

    public List<Node> getRandomConfNodes(int n, List<Node> knownN){
        List<Node> l =  new LinkedList<>(conf.getConfidenceNodes(2));
        return getRandomNodes(n, l, knownN);
    }

    private List<Node> getRandomNodes(int n, List<Node> l, List<Node> knownN) {
        List<Node> resul = new LinkedList<>();

        if(knownN != null){
            l.removeAll(knownN);
        }

        if(l.size() > n) {
            Random rand = new Random(System.currentTimeMillis());
            for (int i = 0; i < n; ++i) {
                resul.add(l.get(rand.nextInt(l.size())));
            }
        }
        else{
            resul = l;
        }
        return resul;
    }

    void removeNodes(ByteString id, Node node){
        Node t = nodes.remove(id);
        assert t != null;
        conf.remove(node);
    }

    public pt.up.fc.dcc.ssd.a.p2p.Node getNodeID(ByteString bArr) {
        return nodes.get(bArr);
    }
}

