package pt.up.fc.dcc.ssd.a.utils;

import java.util.HashMap;

public class ChallengeResponse {
    public boolean ans;
    public HashMap<byte[],String> nodes;

    public ChallengeResponse(boolean ans, HashMap<byte[],String> nodes){
        this.ans = ans;
        this.nodes = nodes;
    }

    public ChallengeResponse(boolean ans){
        this.ans = ans;
    }
}
