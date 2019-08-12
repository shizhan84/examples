import com.sun.tools.attach.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AttachTest {


    public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {


        for (VirtualMachineDescriptor vmd : VirtualMachine.list()) {
            System.out.println(vmd);
            if (vmd.displayName().contains("dw-upload.jar")) {
                VirtualMachine vm = VirtualMachine.attach(vmd);
                vm.loadAgent("/home/springboot/agentmain-1.0-SNAPSHOT.jar");
                System.out.println("loaded");
                vm.detach();

                System.out.println("detached");
                break;
            }
        }


    }
}
