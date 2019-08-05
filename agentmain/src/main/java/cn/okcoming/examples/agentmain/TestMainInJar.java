package cn.okcoming.examples.agentmain;

public class TestMainInJar {
    public static void main(String[] args) throws InterruptedException {
        BizService bizService = new BizService();
        while (true) {
            Thread.sleep(5000);
            bizService.getNumber();
            System.out.println("----------");
        }

    }
}
