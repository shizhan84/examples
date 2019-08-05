package cn.okcoming.examples.agentmain;

import java.util.Random;

public class BizService {

    public int getNumber() {
        try {
            Thread.sleep(new Random().nextInt(1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }
}