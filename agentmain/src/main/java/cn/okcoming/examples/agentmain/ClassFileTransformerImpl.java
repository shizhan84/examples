package cn.okcoming.examples.agentmain;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Objects;

public class ClassFileTransformerImpl implements ClassFileTransformer {


    public byte[] transform(ClassLoader classLoader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain pd, byte[] classfileBuffer) throws IllegalClassFormatException {
        System.out.println("11111:"+className);

        byte[] transformed = null;
        CtClass cl = null;
        try {
            // CtClass、ClassPool、CtMethod、ExprEditor都是javassist提供的字节码操作的类
            ClassPool pool = ClassPool.getDefault();
            cl = pool.makeClass(new ByteArrayInputStream(classfileBuffer));
            CtMethod[] methods = cl.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                System.out.println("22222:"+methods[i].getName());
                if (Objects.equals("getNumber",methods[i].getName())) {

                    methods[i].addLocalVariable("startTime", CtClass.longType);
                    methods[i].insertBefore("startTime = System.currentTimeMillis();");
//			method.insertBefore("long startTime = System.currentTimeMillis();System.out.println(startTime);");
                    methods[i].insertBefore("System.out.println(\"insert before ......\");");
                    methods[i].insertAfter("System.out.println(\"leave " + methods[i].getName() + " and time is :\" + (System.currentTimeMillis() - startTime));");
                }

//                methods[i].instrument(new ExprEditor() {
//
//                    @Override
//                    public void edit(MethodCall m) throws CannotCompileException {
//                        System.out.println("33333:" + m.getClassName() + "." + m.getMethodName());
//                        if (Objects.equals("cn.okcoming.examples.agentmain.BizService",m.getClassName())) {
//
//                            System.out.println("44444:" + m.getClassName() + "." + m.getMethodName());
//                            // 只修改指定的Class
//                            // 把方法体直接替换掉，其中 $proceed($$);是javassist的语法，用来表示原方法体的调用
//                            m.replace("{ long stime = System.currentTimeMillis();" + " $_ = $proceed($$);"
//                                    + "System.out.println(\"" + m.getClassName() + "." + m.getMethodName()
//                                    + " cost:\" + (System.currentTimeMillis() - stime) + \" ms\"); }");
//                        }
//                    }
//                });
            }
            // javassist会把输入的Java代码再编译成字节码byte[]
            transformed = cl.toBytecode();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cl != null) {
                System.out.println("55555:detach:"+className);
                cl.detach();// ClassPool默认不会回收，需要手动清理
            }
        }
        return transformed;

    }
}
