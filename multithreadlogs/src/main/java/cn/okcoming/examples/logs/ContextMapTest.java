package cn.okcoming.examples.logs;


import org.slf4j.MDC;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ContextMapTest {

    //线程池线程或者通过new Thread()都可以
    private static ExecutorService executorService = Executors.newFixedThreadPool(5) ;
    
    public static void main(String args[]) throws InterruptedException{

        MDC.put("uuid", UUID.randomUUID().toString());
        System.out.println(Thread.currentThread().getName() + "-MDC:"  +  MDC.get("uuid"));
        executorService.execute(new Child2Thread( MDC.getCopyOfContextMap()));

        CustomMDC.put("uuid", UUID.randomUUID().toString());
        System.out.println(Thread.currentThread().getName() + "-CustomMDC:"  +  CustomMDC.get("uuid"));
        executorService.execute(new Child1Thread());


        executorService.shutdown();
    }
}