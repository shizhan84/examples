package cn.okcoming.examples.springsingletonprototype;

import cn.okcoming.examples.springsingletonprototype.singleton.Bean3;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AppMain {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext root = new AnnotationConfigApplicationContext("cn.okcoming.examples.springsingletonprototype");


        for (int i = 0; i < 2 ; i++) {
            Bean3 bean2 = root.getBean(Bean3.class);
            bean2.print();
        }
    }
}
