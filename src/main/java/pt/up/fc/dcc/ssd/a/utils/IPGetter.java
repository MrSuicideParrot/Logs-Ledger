package pt.up.fc.dcc.ssd.a.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class IPGetter {

    public static String getIP(){
        try{
            Enumeration<NetworkInterface> netInts = NetworkInterface.getNetworkInterfaces();
            while(netInts.hasMoreElements()) {
                NetworkInterface netInt = netInts.nextElement();
                if(!netInt.isLoopback()){
                    Enumeration<InetAddress> addresses = netInt.getInetAddresses();
                    while(addresses.hasMoreElements()){
                        InetAddress address = addresses.nextElement();
                        if(address instanceof Inet4Address){
                            return address.getHostAddress();
                        }
                    }
                }
            }
        } catch(SocketException e){
            e.printStackTrace();
        }

        return null;
    }
}
