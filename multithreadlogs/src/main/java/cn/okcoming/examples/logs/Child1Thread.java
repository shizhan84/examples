package cn.okcoming.examples.logs;


/**
 * 第一种 实现
 * 使用自定义的mdc来传递上下文环境
 */
public class Child1Thread implements Runnable{



    public void run(){

        System.out.println(Thread.currentThread().getName() + "-CustomMDC:"  + CustomMDC.get("uuid"));
    }
    

}