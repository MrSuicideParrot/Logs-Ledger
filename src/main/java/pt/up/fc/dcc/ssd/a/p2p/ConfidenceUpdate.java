package pt.up.fc.dcc.ssd.a.p2p;

import java.util.TimerTask;

public class ConfidenceUpdate extends TimerTask {

    ConfidenceBuckets conf;

    ConfidenceUpdate(ConfidenceBuckets conf){
        this.conf = conf;
    }

    @Override
    public void run() {
        for (Bucket i:
             conf.buckets) {
            i.updateMistrust();
        }

        System.out.println("Mistrust levels");
        for (Bucket i:
            conf.buckets){
            i.printBucket();
            System.out.println();
        }

    }
}
