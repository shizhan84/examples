package cn.okcoming.examples.logs;


import org.slf4j.MDC;

import java.util.Map;


/**
 * 第二种 实现
 * 使用logback的mdc来传递上下文环境，需要把父线程contextMap通过参数传递进来
 */
public class Child2Thread implements Runnable{

    private Map<String, String> copyOfContextMap;

    public Child2Thread(Map<String, String> copyOfContextMap) {
        this.copyOfContextMap = copyOfContextMap;
    }

    public void run(){
        MDC.setContextMap(copyOfContextMap);
        System.out.println(Thread.currentThread().getName() + "-MDC:"  + MDC.get("uuid"));
    }
    

}