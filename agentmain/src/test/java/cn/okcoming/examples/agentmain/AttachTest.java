package cn.okcoming.examples.agentmain;

import com.sun.tools.attach.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AttachTest {


    public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {


        for (VirtualMachineDescriptor vmd : VirtualMachine.list()) {
            System.out.println(vmd);
            if (vmd.displayName().contains("TestMainInJar")) {
                VirtualMachine vm = VirtualMachine.attach(vmd);
                vm.loadAgent("C:\\work\\wangshizhan\\examples\\agentmain\\target\\agentmain-1.0-SNAPSHOT.jar");
                System.out.println("loaded");
                vm.detach();

                System.out.println("detached");
                break;
            }
        }


    }
}
