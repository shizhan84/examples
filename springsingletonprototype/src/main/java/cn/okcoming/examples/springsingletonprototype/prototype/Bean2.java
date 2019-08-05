package cn.okcoming.examples.springsingletonprototype.prototype;


import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component(value = "bean2")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Bean2 {

    public void print(){
        System.out.println("i am prototype :" + this.getClass().getSimpleName());

    }

}
