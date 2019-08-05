package cn.okcoming.examples.springsingletonprototype.prototype;


import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component(value = "bean1")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE )
public class Bean1 {

    public void print(){
        System.out.println("i am prototype :" + this.getClass().getSimpleName());

    }

}
