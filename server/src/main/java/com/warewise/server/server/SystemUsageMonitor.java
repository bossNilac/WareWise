package com.warewise.server.server;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import com.warewise.common.util.enums.UserRole;
import com.warewise.common.util.protocol.Protocol;

public class SystemUsageMonitor extends Thread {

    private volatile boolean running = true; // Flag to control the thread execution

    private volatile double systemCpuLoad = 0.0;
    private volatile long usedMemory = 0;
    private Server server;

    public void stopMonitoring() {
        running = false;
    }

    public SystemUsageMonitor(Server server){
        this.server= server;
    }

    @Override
    public void run() {
        // Get the OperatingSystemMXBean from the ManagementFactory
        OperatingSystemMXBean osBean =
                (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        Runtime runtime = Runtime.getRuntime();

        while (running) {
            // Store CPU usage
            systemCpuLoad = osBean.getCpuLoad() * 100;   // Convert to percentage
            usedMemory = (runtime.totalMemory() - runtime.freeMemory() ) / (1024 * 1024);
            // Wait before checking again (1 second)
            try {
                Thread.sleep(1000);
                sendBenchmark(systemCpuLoad,usedMemory);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void sendBenchmark(double systemCpuLoad , double usedMemory ) {
        for (ServerConnection conn : server.getConnections()){
                conn.sendMessage(Protocol.SEND_BENCHMARK + Protocol.SEPARATOR + systemCpuLoad
                        + Protocol.SEPARATOR + usedMemory
                        + Protocol.SEPARATOR + server.getConnections().size());
        }
    }

}
