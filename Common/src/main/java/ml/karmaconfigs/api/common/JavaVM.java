package ml.karmaconfigs.api.common;

/*
 * This file is part of KarmaAPI, licensed under the MIT License.
 *
 *  Copyright (c) karma (KarmaDev) <karmaconfigs@gmail.com>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 * Java virtual machine
 */
public final class JavaVM {

    /**
     * Get the operative system name
     *
     * @return the os name
     */
    public static String osName() {
        String os = System.getProperty("os.name");
        return os.substring(0, 1).toUpperCase() + os.substring(1).toLowerCase();
    }

    /**
     * Get the operative system version
     *
     * @return the os version
     */
    public static String osVersion() {
        return System.getProperty("os.version");
    }

    /**
     * Get the operative system model
     *
     * @return the os model
     */
    public static String osModel() {
        return System.getProperty("sun.arch.data.model");
    }

    /**
     * Get the operative system architecture
     *
     * @return the os architecture
     */
    public static String osArchitecture() {
        String arch = System.getenv("PROCESSOR_ARCHITECTURE");
        String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");
        String lastTry = System.getProperty("sun.cpu.isalist");
        return (arch != null) ? arch : ((wow64Arch != null) ? wow64Arch : ((lastTry != null) ? lastTry : jvmArchitecture()));
    }

    /**
     * Get the operative system max memory
     *
     * @return the os max memory
     */
    public static String osMaxMemory() {
        try {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            Object attribute = mBeanServer.getAttribute(new ObjectName("java.lang", "type", "OperatingSystem"), "TotalPhysicalMemorySize");
            long max = Long.parseLong(attribute.toString());
            return (int) (max / 1024 / 1024 / 1024 + 1) + "GB";
        } catch (Throwable ex) {
            return "unable to allocate memory";
        }
    }

    /**
     * Get the operative system free memory
     *
     * @return the os free memory
     */
    public static String osFreeMemory() {
        try {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            Object attribute = mBeanServer.getAttribute(new ObjectName("java.lang", "type", "OperatingSystem"), "FreePhysicalMemorySize");
            long available = Long.parseLong(attribute.toString());
            return (int) (available / 1024 / 1024 / 1024 + 1) + "GB";
        } catch (Throwable ex) {
            return "unable to allocate memory";
        }
    }

    /**
     * Get the java virtual machine architecture
     *
     * @return the jvm architecture
     */
    public static String jvmArchitecture() {
        return System.getProperty("os.arch");
    }

    /**
     * Get the java virtual machine max memory
     *
     * @return the jvm max memory
     */
    public static String jvmMax() {
        return Runtime.getRuntime().maxMemory() / 1024 / 1024 + " MB";
    }

    /**
     * Get the java virtual machine available memory
     *
     * @return the jvm available memory
     */
    public static String jvmAvailable() {
        return Runtime.getRuntime().freeMemory() / 1024 / 1024 + " MB";
    }

    /**
     * Get the amount of processors the java virtual
     * machine has
     *
     * @return the jvm processors
     */
    public static int jvmProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * Get the java virtual machine version
     *
     * @return the java version
     */
    public static int javaVersion() {
        String version = System.getProperty("java.version");
        if(version.startsWith("1.")) {
            version = version.substring(2, 3);
        } else {
            int dot = version.indexOf(".");
            if(dot != -1) { version = version.substring(0, dot); }
        } return Integer.parseInt(version);
    }
}
