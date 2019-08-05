package cn.okcoming.examples.springcontexthierarchy;

import cn.okcoming.examples.springcontexthierarchy.biz1.Bean1;
import cn.okcoming.examples.springcontexthierarchy.biz2.Bean2;
import cn.okcoming.examples.springcontexthierarchy.common.BeanRegistry;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AppMain {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext root = new AnnotationConfigApplicationContext("cn.okcoming.cn.okcoming.examples.springcontexthierarchy.common");
        BeanRegistry beanRegistry = root.getBean(BeanRegistry.class);

        AnnotationConfigApplicationContext biz1 = new AnnotationConfigApplicationContext();
        biz1.setParent(root);
        biz1.scan("cn.okcoming.cn.okcoming.examples.springcontexthierarchy.biz1");
        biz1.refresh();

        Bean1 bean1 = biz1.getBean(Bean1.class);
        beanRegistry.put("bean1",bean1);

        AnnotationConfigApplicationContext biz2 = new AnnotationConfigApplicationContext();
        biz2.setParent(root);
        biz2.scan("cn.okcoming.cn.okcoming.examples.springcontexthierarchy.biz2");
        biz2.refresh();
        Bean2 bean2 = biz2.getBean(Bean2.class);
        beanRegistry.put("bean2",bean2);
        bean1.print();

        /**
         * 通过监听指定文件夹中class文件是否有变化来重新加载类
         * 关闭原来的子容器，重新加载一个
         */
        biz2.close();
        biz2 = new AnnotationConfigApplicationContext();
        biz2.setParent(root);
        biz2.scan("cn.okcoming.cn.okcoming.examples.springcontexthierarchy.biz2");
        biz2.refresh();
        bean2 = biz2.getBean(Bean2.class);
        beanRegistry.put("bean2",bean2);

        bean1.print();

    }
}
