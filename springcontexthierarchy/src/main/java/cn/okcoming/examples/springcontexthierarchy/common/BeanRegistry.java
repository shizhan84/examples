package cn.okcoming.examples.springcontexthierarchy.common;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BeanRegistry {
    private Map<String,Object> holder= new HashMap();

    public Object put(String name,Object object){
        return holder.putIfAbsent(name,object);
    }

    public <T> T get(String name){
        return (T)holder.get(name);
    }
}
