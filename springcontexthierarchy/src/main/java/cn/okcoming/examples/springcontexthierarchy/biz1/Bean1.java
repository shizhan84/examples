package cn.okcoming.examples.springcontexthierarchy.biz1;


import cn.okcoming.examples.springcontexthierarchy.biz2.Bean2;
import cn.okcoming.examples.springcontexthierarchy.common.BeanRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component(value = "bean1")
public class Bean1 {
    @Autowired
    private BeanRegistry beanRegistry;

    public void print(){
        System.out.println("i am bean1");
        Bean2 bean2 =  beanRegistry.get("bean2");
        bean2.print();
    }

    @PreDestroy
    public void destory(){
        System.out.println("bean1 destory");
    }
}
