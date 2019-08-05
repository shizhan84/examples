package cn.okcoming.examples.springcontexthierarchy.biz2;

import org.springframework.stereotype.Component;

@Component(value = "bean2")
public class Bean2  {
    public void print(){
        System.out.println("i am  bean2 ");
    }
}
