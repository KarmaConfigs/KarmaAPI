package ml.karmaconfigs.api.common;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

public final class JavaVM {
    public static String osName() {
        String os = System.getProperty("os.name");
        return os.substring(0, 1).toUpperCase() + os.substring(1).toLowerCase();
    }

    public static String osVersion() {
        return System.getProperty("os.version");
    }

    public static String osModel() {
        return System.getProperty("sun.arch.data.model");
    }

    public static String osArchitecture() {
        String arch = System.getenv("PROCESSOR_ARCHITECTURE");
        String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");
        String lastTry = System.getProperty("sun.cpu.isalist");
        return (arch != null) ? arch : ((wow64Arch != null) ? wow64Arch : ((lastTry != null) ? lastTry : jvmArchitecture()));
    }

    public static String osMaxMemory() {
        try {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            Object attribute = mBeanServer.getAttribute(new ObjectName("java.lang", "type", "OperatingSystem"), "TotalPhysicalMemorySize");
            long max = Long.parseLong(attribute.toString());
            return (int) (max / 1024L / 1024L / 1024L + 1L) + "GB";
        } catch (Throwable ex) {
            return "unable to allocate memory";
        }
    }

    public static String osFreeMemory() {
        try {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            Object attribute = mBeanServer.getAttribute(new ObjectName("java.lang", "type", "OperatingSystem"), "FreePhysicalMemorySize");
            long available = Long.parseLong(attribute.toString());
            return (int) (available / 1024L / 1024L / 1024L + 1L) + "GB";
        } catch (Throwable ex) {
            return "unable to allocate memory";
        }
    }

    public static String jvmArchitecture() {
        return System.getProperty("os.arch");
    }

    public static String jvmAllocated() {
        long allocated = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        return (allocated / 1024L / 1024L) + "GB";
    }
}
