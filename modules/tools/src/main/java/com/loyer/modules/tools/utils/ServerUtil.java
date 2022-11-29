package com.loyer.modules.tools.utils;

import com.loyer.common.core.enums.CalculateType;
import com.loyer.common.core.enums.DatePattern;
import com.loyer.common.core.utils.common.CalculateUtil;
import com.loyer.common.core.utils.common.DateUtil;
import com.loyer.common.dedicine.entity.ApiResult;
import lombok.Data;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.CentralProcessor.TickType;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.util.Util;

import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * 服务器工具类
 *
 * @author kuangq
 * @date 2020-08-16 18:44
 */
public class ServerUtil {

    /**
     * 获取当前服务器信息
     *
     * @author kuangq
     * @date 2020-08-16 22:58
     */
    public static ApiResult getSreverInfo() {
        Map<String, Object> data = new HashMap<>(5);
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();
        data.put("processor", new Processor(hardware.getProcessor()));
        data.put("memory", new Memory(hardware.getMemory()));
        Properties properties = System.getProperties();
        data.put("jvm", new Jvm(properties));
        data.put("computer", new Computer(properties));
        FileSystem fileSystem = new SystemInfo().getOperatingSystem().getFileSystem();
        List<Disk> diskList = new ArrayList<Disk>() {{
            for (OSFileStore osFileStore : fileSystem.getFileStores()) {
                add(new Disk(osFileStore));
            }
        }};
        data.put("diskList", diskList);
        return ApiResult.success(data);
    }

    /**
     * 单位转换（为保证精度，先做四位舍入，再保存两位）
     *
     * @author kuangq
     * @date 2020-08-16 21:29
     */
    private static double format(double number, int power) {
        double decimal = CalculateUtil.actuarial(new BigDecimal(number), BigDecimal.valueOf(Math.pow(1024, power)), CalculateType.DIVIDE);
        double result = CalculateUtil.round(decimal, 4);
        return CalculateUtil.round(result, 2);
    }

    /**
     * 计算百分比
     *
     * @author kuangq
     * @date 2020-08-17 13:13
     */
    private static double calculateRate(double molecule, double denominator) {
        double decimal = CalculateUtil.actuarial(molecule, denominator, CalculateType.DIVIDE) * 100;
        double result = CalculateUtil.round(decimal, 4);
        return CalculateUtil.round(result, 2);
    }

    @Data
    public static class Processor {

        //线程数
        private int threadNum;
        //用户使用率
        private double userUsageRate;
        //系统使用率
        private double systemUsageRate;
        //空闲率
        private double surplusRate;

        /**
         * 从CentralProcessor对象获取处理器信息
         *
         * @author kuangq
         * @date 2020-08-16 22:27
         */
        public Processor(CentralProcessor centralProcessor) {
            long[] prevTicks = centralProcessor.getSystemCpuLoadTicks();
            Util.sleep(1000);
            long[] ticks = centralProcessor.getSystemCpuLoadTicks();
            long idle = ticks[TickType.IDLE.getIndex()] - prevTicks[TickType.IDLE.getIndex()];
            long iowait = ticks[TickType.IOWAIT.getIndex()] - prevTicks[TickType.IOWAIT.getIndex()];
            long irq = ticks[TickType.IRQ.getIndex()] - prevTicks[TickType.IRQ.getIndex()];
            long nice = ticks[TickType.NICE.getIndex()] - prevTicks[TickType.NICE.getIndex()];
            long softirq = ticks[TickType.SOFTIRQ.getIndex()] - prevTicks[TickType.SOFTIRQ.getIndex()];
            long steal = ticks[TickType.STEAL.getIndex()] - prevTicks[TickType.STEAL.getIndex()];
            long system = ticks[TickType.SYSTEM.getIndex()] - prevTicks[TickType.SYSTEM.getIndex()];
            long user = ticks[TickType.USER.getIndex()] - prevTicks[TickType.USER.getIndex()];
            long total = idle + iowait + irq + nice + softirq + steal + system + user;
            this.threadNum = centralProcessor.getLogicalProcessorCount();
            this.userUsageRate = calculateRate(user, total);
            this.systemUsageRate = calculateRate(system, total);
            this.surplusRate = calculateRate(idle, total);
        }
    }

    @Data
    public static class Memory {

        //总的内存(G)
        private double gross;
        //剩余内存(G)
        private double surplus;
        //已用内存(G)
        private double employ;
        //使用率
        private double usageRate;

        /**
         * 从GlobalMemory对象中获取内存信息
         *
         * @author kuangq
         * @date 2020-08-16 21:31
         */
        public Memory(GlobalMemory globalMemory) {
            long total = globalMemory.getTotal();
            long available = globalMemory.getAvailable();
            long used = total - available;
            this.gross = format(total, 3);
            this.surplus = format(available, 3);
            this.employ = format(used, 3);
            this.usageRate = calculateRate(employ, gross);
        }
    }

    @Data
    public static class Jvm {

        //JVM总的内存(M)
        private double gross;
        //JVM剩余内存(M)
        private double surplus;
        //JVM已用内存(M)
        private double employ;
        //JVM使用率
        private double usageRate;
        //JDK版本
        private String version;
        //JDK路径
        private String home;
        //JDK名称
        private String name;
        //JDK启动时间
        private String startTime;
        //JDK运行时间
        private String runTime;

        /**
         * 从Properties、ManagementFactory对象获取JVM信息
         *
         * @author kuangq
         * @date 2020-08-17 11:07
         */
        public Jvm(Properties properties) {
            long total = Runtime.getRuntime().totalMemory();
            long free = Runtime.getRuntime().freeMemory();
            long used = total - free;
            this.gross = format(total, 2);
            this.surplus = format(free, 2);
            this.employ = format(used, 2);
            this.usageRate = calculateRate(employ, gross);
            this.version = properties.getProperty("java.version");
            this.home = properties.getProperty("java.home");
            this.name = ManagementFactory.getRuntimeMXBean().getVmName();
            long startTime = ManagementFactory.getRuntimeMXBean().getStartTime();
            this.startTime = DateUtil.getTimestamp(startTime, DatePattern.YMD_HM_1);
            this.runTime = DateUtil.getTdoaStr(startTime);
        }
    }

    @Data
    public static class Computer {

        //服务器名称
        private String name;
        //服务器IP
        private String ip;
        //操作系统
        private String osName;
        //系统架构
        private String osArchitecture;
        //项目路径
        private String projectPath;

        /**
         * 从Properties对象获取服务器信息
         *
         * @author kuangq
         * @date 2020-08-17 11:16
         */
        public Computer(Properties properties) {
            try {
                this.name = InetAddress.getLocalHost().getHostName();
                this.ip = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                this.name = "未知";
                this.ip = "127.0.0.1";
            }
            this.osName = properties.getProperty("os.name");
            this.osArchitecture = properties.getProperty("os.arch");
            this.projectPath = properties.getProperty("user.dir");
        }
    }

    @Data
    public static class Disk {

        //盘符名称
        private String name;
        //盘符类型
        private String type;
        //盘符路径
        private String path;
        //总大小(G)
        private double gross;
        //剩余大小(G)
        private double surplus;
        //已经使用(G)
        private double employ;
        //使用率
        private double usageRate;

        /**
         * 从OSFileStore对象中获取单个磁盘信息
         *
         * @author kuangq
         * @date 2020-08-17 11:31
         */
        public Disk(OSFileStore osFileStore) {
            this.name = osFileStore.getName();
            this.type = osFileStore.getType();
            this.path = osFileStore.getMount();
            long total = osFileStore.getTotalSpace();
            long available = osFileStore.getUsableSpace();
            long used = total - available;
            this.gross = format(total, 3);
            this.surplus = format(available, 3);
            this.employ = format(used, 3);
            this.usageRate = calculateRate(employ, gross);
        }
    }
}