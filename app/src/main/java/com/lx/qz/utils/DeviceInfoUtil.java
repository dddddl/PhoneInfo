package com.lx.qz.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by LENOVO on 2018/9/6.
 */

public class DeviceInfoUtil {

    //IMEI
    @SuppressLint("MissingPermission")
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        if (telephonyManager == null) {
            return "未知";
        }
        String imei = "未知";
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                imei = telephonyManager.getImei();
            } else {
                imei = telephonyManager.getDeviceId();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return imei;
    }

    //MEID
    @SuppressLint("MissingPermission")
    public static String getMEID(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        if (telephonyManager == null) {
            return "未知";
        }
        String meid = "未知";
        if (Build.VERSION.SDK_INT < 21) {
            if (GetSystemInfoUtil.getNumber(context) == 14) {
                meid = GetSystemInfoUtil.getImeiOrMeid(context);
            }
        } else if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT < 26) {
            Map<String, String> map = GetSystemInfoUtil.getImeiAndMeid(context);
            if (!"".equals(map.get("meid")))
                meid = map.get("meid");
        } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            meid = telephonyManager.getMeid();
        }

        return meid;
    }


    @SuppressLint("MissingPermission")
    public static String getIMSI(Context context) {
        String imsi = "";
        try {   //普通方法获取imsi
            TelephonyManager tm = (TelephonyManager) context.
                    getSystemService(Context.TELEPHONY_SERVICE);
            imsi = tm.getSubscriberId();
            if (imsi == null || "".equals(imsi)) imsi = tm.getSimOperator();
            Class<?>[] resources = new Class<?>[]{int.class};
            Integer resourcesId = new Integer(1);
            if (imsi == null || "".equals(imsi)) {
                try {   //利用反射获取    MTK手机
                    Method addMethod = tm.getClass().getDeclaredMethod("getSubscriberIdGemini", resources);
                    addMethod.setAccessible(true);
                    imsi = (String) addMethod.invoke(tm, resourcesId);
                } catch (Exception e) {
                    imsi = null;
                }
            }
            if (imsi == null || "".equals(imsi)) {
                try {   //利用反射获取    展讯手机
                    Class<?> c = Class
                            .forName("com.android.internal.telephony.PhoneFactory");
                    Method m = c.getMethod("getServiceName", String.class, int.class);
                    String spreadTmService = (String) m.invoke(c, Context.TELEPHONY_SERVICE, 1);
                    TelephonyManager tm1 = (TelephonyManager) context.getSystemService(spreadTmService);
                    imsi = tm1.getSubscriberId();
                } catch (Exception e) {
                    imsi = null;
                }
            }
            if (imsi == null || "".equals(imsi)) {
                try {   //利用反射获取    高通手机
                    Method addMethod2 = tm.getClass().getDeclaredMethod("getSimSerialNumber", resources);
                    addMethod2.setAccessible(true);
                    imsi = (String) addMethod2.invoke(tm, resourcesId);
                } catch (Exception e) {
                    imsi = null;
                }
            }
            if (imsi == null || "".equals(imsi)) {
                imsi = "未知";
            }
            return imsi;
        } catch (Exception e) {
            return "未知";
        }
    }

    /**
     * 返回手机运营商名称
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    public static String getProvidersName(Context context) {
        String ProvidersName = null;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String IMSI = telephonyManager.getSubscriberId();
        if (IMSI == null) {
            return "未知";
        }

        if (IMSI.startsWith("46000") || IMSI.startsWith("46002") || IMSI.startsWith("46007")) {
            ProvidersName = "中国移动";
        } else if (IMSI.startsWith("46001") || IMSI.startsWith("46006")) {
            ProvidersName = "中国联通";
        } else if (IMSI.startsWith("46003") || IMSI.startsWith("46005") || IMSI.startsWith("46011")) {
            ProvidersName = "中国电信";
        }

        return ProvidersName;
    }

    /**
     * BASEBAND-VER
     * 基带版本
     * return String
     */

    public static String getBasebandVersion() {
        String Version = "";
        try {
            Class cl = Class.forName("android.os.SystemProperties");
            Object invoker = cl.newInstance();
            Method m = cl.getMethod("get", new Class[]{String.class, String.class});
            Object result = m.invoke(invoker, new Object[]{"gsm.version.baseband", "no message"});
            Version = (String) result;
        } catch (Exception e) {
        }
        return Version;
    }

    //蓝牙地址
    public static String getBtAddressByReflection() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Field field = null;
        try {
            field = BluetoothAdapter.class.getDeclaredField("mService");
            field.setAccessible(true);
            Object bluetoothManagerService = field.get(bluetoothAdapter);
            if (bluetoothManagerService == null) {
                return "未知";
            }
            Method method = bluetoothManagerService.getClass().getMethod("getAddress");
            if (method != null) {
                Object obj = method.invoke(bluetoothManagerService);
                if (obj != null) {
                    return obj.toString();
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return "未知";
    }

    public static String getMacAddressFromIp(Context context) {
        String mac_s = "";
        StringBuilder buf = new StringBuilder();
        try {
            byte[] mac;
            NetworkInterface ne = NetworkInterface.getByInetAddress(InetAddress.getByName(getIpAddress(context)));
            mac = ne.getHardwareAddress();
            for (byte b : mac) {
                buf.append(String.format("%02X:", b));
            }
            if (buf.length() > 0) {
                buf.deleteCharAt(buf.length() - 1);
            }
            mac_s = buf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mac_s;
    }

    public static String getIpAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            // 3/4g网络
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                //  wifi网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());
                return ipAddress;
            } else if (info.getType() == ConnectivityManager.TYPE_ETHERNET) {
                // 有限网络
                return getLocalIp();
            }
        }
        return null;
    }

    private static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }


    // 获取有限网IP
    private static String getLocalIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {

        }
        return "0.0.0.0";

    }

    //获取系统默认时区
    public static String getTimeZone() {
        return TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT);
    }

    //获取系统默认时区名
    public static String getTimeZoneId() {
        return TimeZone.getDefault().getID();
    }

    public static String getCPUABI() {
        return Build.CPU_ABI + "/" + Build.CPU_ABI2;
    }

    public static String getBoard() {
        return Build.BOARD;
    }


    public static String getStoragePath(Context mContext, boolean is_removale) {

        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Method getState = storageVolumeClazz.getMethod("getState");

            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                String state = (String) getState.invoke(storageVolumeElement);
                if (is_removale == removable) {
                    return "unmounted".equals(state) ? "" : path;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getHardWare() {
        return Build.HARDWARE;
    }

    public static String getAndroidId(Context context) {
        return Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
    }

    /**
     * 判断SD卡是否可用
     *
     * @return true : 可用<br>false : 不可用
     */
    public static boolean isSDCardEnable(Context context) {
//        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        String path = getStoragePath(context, true);
        return "".equals(path) ? false : true;
    }

    //获取用户空间总大小
    //TODO
    public static String getTotalUserSpace(Context context) {
        if (isSDCardEnable(context)) {
            long totalSize = getTotalExternalMemorySize(context) + getInternalToatalSpaceOfLong(context);
            return Formatter.formatFileSize(context, totalSize);
        } else {
            return getInternalToatalSpace(context);
        }
    }

    //获取用户空间可用大小
    //TODO
    public static String getTotalAvailableUserSpace(Context context) {
        if (isSDCardEnable(context)) {
            long totalSize = getFreeSpace(context) + getAvailableInternalMemorySizeOfLong(context);
            return Formatter.formatFileSize(context, totalSize);
        } else {
            return getAvailableInternalMemorySize(context);
        }
    }

    /**
     * 获取手机外部总空间大小
     *
     * @return 总大小，字节为单位
     */
    static public long getTotalExternalMemorySize(Context context) {
        if (isSDCardEnable(context)) {
            //获取SDCard根目录
//            File path = Environment.getExternalStorageDirectory();
//            StatFs stat = new StatFs(path.getPath());
            StatFs stat = new StatFs(getStoragePath(context, true));
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        } else {
            return 0L;
        }
    }

    /**
     * 获取SD卡剩余空间
     *
     * @return SD卡剩余空间
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static long getFreeSpace(Context context) {
        if (!isSDCardEnable(context)) return 0L;
        StatFs stat = new StatFs(getStoragePath(context, true));
        long blockSize, availableBlocks;
        availableBlocks = stat.getAvailableBlocksLong();
        blockSize = stat.getBlockSizeLong();
        long size = availableBlocks * blockSize;
        return size;
    }

    /**
     * 获取内置存储空间的总容量
     *
     * @param context
     * @return
     */
    public static String getInternalToatalSpace(Context context) {
        String path = Environment.getDataDirectory().getPath();
        StatFs statFs = new StatFs(path);
        long blockSize = statFs.getBlockSize();
        long totalBlocks = statFs.getBlockCount();
        long availableBlocks = statFs.getAvailableBlocks();
        long useBlocks = totalBlocks - availableBlocks;
        long rom_length = totalBlocks * blockSize;
        return Formatter.formatFileSize(context, rom_length);
    }

    public static long getInternalToatalSpaceOfLong(Context context) {
        String path = Environment.getDataDirectory().getPath();
        StatFs statFs = new StatFs(path);
        long blockSize = statFs.getBlockSize();
        long totalBlocks = statFs.getBlockCount();
        long availableBlocks = statFs.getAvailableBlocks();
        long useBlocks = totalBlocks - availableBlocks;
        long rom_length = totalBlocks * blockSize;
        return rom_length;
    }

    //获取内置存储空间可用大小

    /**
     * 获取手机内部可用空间大小
     *
     * @return 大小，字节为单位
     */
    static public String getAvailableInternalMemorySize(Context context) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        //获取可用区块数量
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(context, availableBlocks * blockSize);
    }

    static public long getAvailableInternalMemorySizeOfLong(Context context) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        //获取可用区块数量
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    //序列号
    public static String getSerialNumber() {
        return Build.SERIAL;
    }

    /**
     * 获取手机号 取出MSISDN，很可能为空
     * @return
     */
//    @SuppressLint("MissingPermission")
//    public static String getPhoneNumber(Context context){
//        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        return tm.getLine1Number();
//    }


    /**
     * ICCID:ICC identity集成电路卡标识，这个是唯一标识一张卡片物理号码的
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    public static String getIccid(Context context) {
        if (isSimReady(context)) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getSimSerialNumber();
        }
        return "未知";
    }

    /**
     * 判断SIM卡是否准备好
     *
     * @param context
     * @return
     */
    public static boolean isSimReady(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            int simState = tm.getSimState();
            if (simState == TelephonyManager.SIM_STATE_READY) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    //获取手机品牌
    public static String getBrand() {
        return Build.BRAND;
    }

    //获取手机型号
    public static String getModel() {
        return Build.MODEL;
    }

    //ip地址
    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return "未知";
    }

    //内核版本
    public static String getKernelVersion() {
        String kernelVersion = "未知";
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream("/proc/version");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return kernelVersion;
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 8 * 1024);
        String info = "";
        String line = "";
        try {
            while ((line = bufferedReader.readLine()) != null) {
                info += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            if (info != "") {
                final String keyword = "version ";
                int index = info.indexOf(keyword);
                line = info.substring(index + keyword.length());
                index = line.indexOf(" ");
                kernelVersion = line.substring(0, index);
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return kernelVersion;
    }


}
