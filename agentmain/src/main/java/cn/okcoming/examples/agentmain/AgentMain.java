package cn.okcoming.examples.agentmain;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public class AgentMain {
    public static void agentmain(String agentArgs, Instrumentation inst)
            throws ClassNotFoundException, UnmodifiableClassException{
        System.out.println("Agent Main Done");
        inst.addTransformer(new ClassFileTransformerImpl(), true);
        Class[] classes = inst.getAllLoadedClasses();
        for (Class clazz : classes){
            if(clazz.getName().contains("DataController")){
                System.out.println(clazz.getName());
                inst.retransformClasses(clazz);
            }
        }
    }
}
