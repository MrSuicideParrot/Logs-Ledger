package pt.up.fc.dcc.ssd.a.logs;

import java.security.SecureRandom;

public class RandomLogGenerator implements Runnable{

    public int min;
    public int max;

    public RandomLogGenerator(){
        this.min = 5000;
        this.max = 30000;
    }

    public RandomLogGenerator(int min, int max){
        this.min = min*1000;
        this.max = max*1000;
    }

    @Override
    public void run(){
        int sleepTime;
        SecureRandom rand = new SecureRandom();
        while (true){
            try {

                sleepTime = rand.nextInt(max-min+1)+min;
                System.out.println("Next log will be created in "+ (float)sleepTime/1000 + "seconds.");
                Thread.sleep(sleepTime);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
