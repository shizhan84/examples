package cn.okcoming.examples.springsingletonprototype.singleton;

import cn.okcoming.examples.springsingletonprototype.prototype.Bean1;
import cn.okcoming.examples.springsingletonprototype.prototype.Bean2;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;


@Component(value = "bean3")
public class Bean3 {
    @Autowired
    private ObjectFactory<Bean1> beanF; //第一种办法

    @Lookup(value = "bean2")
    public Bean2 getBean2(){//第二种办法
        return null;
    }

    public void print(){
        System.out.println("i am  singleton : " + this.getClass().getSimpleName());
        Bean1 bean1 = beanF.getObject();
        bean1.print();
        Bean2 bean2 = getBean2();
        bean2.print();
    }



}
