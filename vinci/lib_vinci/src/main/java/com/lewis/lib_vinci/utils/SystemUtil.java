package com.lewis.lib_vinci.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.lewis.lib_vinci.LibConstants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

/**
 * 关于系统信息操作的工具类
 * Date: 2016-10-17
 * Time: 18:14
 * FIXME
 */
public class SystemUtil {
    private static final String TAG = SystemUtil.class.getSimpleName();
    private static final String PREFERENCES_NAME = "_pay.preferences";
    private static CPUInfo systemCPUInfo = null;
    private static final String IMEI = "imei";

    public SystemUtil() {
    }

    /**
     * 创建自定义发用户代理
     *
     * @param context
     * @return
     */
    public static String getUserAgent(Context context) {
        String webUserAgent = null;
        if (context != null) {
            try {
                Class locale = Class.forName("com.android.internal.R$string");
                Field buffer = locale.getDeclaredField("web_user_agent");
                Integer version = (Integer) buffer.get((Object) null);
                webUserAgent = context.getString(version.intValue());
            } catch (Throwable var7) {

            }
        }

        if (TextUtils.isEmpty(webUserAgent)) {
            webUserAgent = "Mozilla/5.0 (Linux; U; Android %s) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 %sSafari/533.1";
        }

        Locale locale1 = Locale.getDefault();
        StringBuffer buffer1 = new StringBuffer();
        //当前宿主手机的操作系统版本（Android4.4等）
        String version1 = Build.VERSION.RELEASE;
        if (version1.length() > 0) {
            buffer1.append(version1);
        } else {
            buffer1.append("1.0");
        }

        buffer1.append("; ");
        String language = locale1.getLanguage();
        String id;
        if (language != null) {
            buffer1.append(language.toLowerCase());
            id = locale1.getCountry();
            if (id != null) {
                buffer1.append("-");
                buffer1.append(id.toLowerCase());
            }
        } else {
            buffer1.append("en");
        }

        if ("REL".equals(Build.VERSION.CODENAME)) {
            //手机型号
            id = Build.MODEL;
            if (id.length() > 0) {
                buffer1.append("; ");
                buffer1.append(id);
            }
        }

        id = Build.ID;
        if (id.length() > 0) {
            buffer1.append(" Build/");
            buffer1.append(id);
        }

        return String.format(webUserAgent, new Object[]{buffer1, "Mobile "});
    }

    /**
     * 获取指定名称的外部存储路径
     *
     * @param context
     * @param dirName 指定的文件夹名称
     * @return
     */
    public static String getDiskCacheDir(Context context, String dirName) {
        String cachePath = null;
        File cacheDir;
        if ("mounted".equals(Environment.getExternalStorageState())) {
            cacheDir = context.getExternalCacheDir();
            if (cacheDir != null) {
                cachePath = cacheDir.getPath();
            }
        }

        if (cachePath == null) {
            cacheDir = context.getCacheDir();
            if (cacheDir != null && cacheDir.exists()) {
                cachePath = cacheDir.getPath();
            }
        }

        return cachePath + File.separator + dirName;
    }

    /**
     * 获取指定文件的空间大小
     *
     * @param dir
     * @return
     */
    public static long getAvailableSpace(File dir) {
        try {
            StatFs e = new StatFs(dir.getPath());
            return (long) e.getBlockSize() * (long) e.getAvailableBlocks();
        } catch (Throwable var2) {
            Log.e(var2.getMessage(), var2.toString(), var2);
            return -1L;
        }
    }


    /**
     * 从Assets文件夹读取文件
     *
     * @param context
     * @param name    指定文件名称
     * @return
     */
    public static String getAssetContent(Context context, String name) {
        InputStream in = null;
        try {
            in = context.getAssets().open(name);
            int size = in.available();
            byte[] buffer = new byte[size];
            in.read(buffer);
            in.close();
            return new String(buffer);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * @param context context
     * @return imsi
     */
    public static String getImsi(Context context) {
        TelephonyManager telManager = (TelephonyManager) context.getApplicationContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (telManager != null) {
            String imsi = telManager.getSubscriberId();
            if (!TextUtils.isEmpty(imsi)) {
                return imsi;
            }
        }
        return "";
    }

    /**
     * @param context context
     * @return imei
     */
    public static String getImei(Context context) {
        String imei;
        TelephonyManager mTelephonyMgr = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephonyMgr == null) {
            imei = makeImei(context);
        } else {
            imei = mTelephonyMgr.getDeviceId();
            if (imei == null || imei.length() < 2) {
                imei = makeImei(context);
            } else {
                int count = imei.length();
                char ch = imei.charAt(0);
                int i;
                for (i = 1; i < count; i++) {
                    if (ch != imei.charAt(i)) {
                        break;
                    }
                }
                if (i >= count) {
                    imei = makeImei(context);
                }
            }
        }
        if (LibConstants.DEBUG) {
            Log.d(TAG, "imei=" + imei + "#len=" + imei.length());
        }
        return imei;
    }

    /**
     * 从指定的SP文件PREFERENCES_NAME根据原生的IMEI号  创建自定义的IMEI号
     *
     * @param con
     * @return
     */
    private static String makeImei(Context con) {
        String imei = (String) SpUtils.getParam(con, PREFERENCES_NAME, IMEI, "");

        if (TextUtils.isEmpty(imei)) {
            StringBuffer sb = new StringBuffer();
            sb.append("CREDOO");
            String src;
            long now = System.currentTimeMillis();
            src = Long.toHexString(now).toUpperCase();
            int count = src.length();
            Random r = new Random(now);
            if (count < 7) {
                for (; count < 7; count++) {
                    src += (char) (r.nextInt(10) | 0x30);
                }
                r = null;
            }
            if (LibConstants.DEBUG) {
                Log.d(TAG, "makeImei :: " + src + " # " + count);
            }
            int len = src.length();
            for (int j = len - 1; j >= len - 6; j--) {
                sb.append(src.charAt(j));
            }
            for (int j = sb.length(); j < 15; j++) {
                sb.append((char) (r.nextInt(10) | 0x30));
            }
            SpUtils.setParam(con, PREFERENCES_NAME, IMEI, sb.toString());
            return sb.toString();
        } else {
            if (LibConstants.DEBUG) {
                Log.d(TAG, "从文件里面获取imei号=" + imei);
            }
            return imei;
        }
    }


    /**
     * 获取当前手机系统版本（Android4.4）
     *
     * @return
     */
    public static String getOsVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取当前手机型号
     *
     * @return
     */
    public static String getDeviceName() {
        return Build.MODEL;
    }

    /**
     * 获取手机WLAN mac地址
     *
     * @param context context
     * @return mac address
     */
//    public static String getMacAddress(Context context) {
//        try {
//            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//            if (wifi != null) {
//                WifiInfo info = wifi.getConnectionInfo();
//                return info.getMacAddress();
//            }
//        } catch (Exception e) {
//            if (LibConstants.DEBUG) {
//                Log.d(TAG, e.toString());
//            }
//        }
//        return "";
//    }

    /**
     * 获取指定格式的UserAgent
     *
     * @param context context
     * @return ua
     */
    @SuppressWarnings("deprecation")
    public static String getUA(Context context) {
        String vn = "";
        String vc = "";
        try {
            PackageManager manager = context.getPackageManager();
            if (manager != null) {
                PackageInfo info;
                info = manager.getPackageInfo(context.getPackageName(), 0);
                if (info != null) {
                    vn = info.versionName;
                    vc = info.versionCode + "";
                }
            }
        } catch (Exception e) {
            if (vn == null) {
                vn = "";
            }
            if (LibConstants.DEBUG) {
                Log.d(TAG, e.toString());
                e.printStackTrace();
            }
        }
        // ua=bfbsdk_720_1200_15_4.0.4Lenovo-K860-stuttgart_1.0_1
        DisplayMetrics displayer = context.getResources().getDisplayMetrics();
        StringBuilder sb = new StringBuilder();
        sb.append(LibConstants.SDK_VERSION);
        sb.append('_');
        sb.append(displayer.widthPixels);
        sb.append('_');
        sb.append(displayer.heightPixels);
        sb.append('_');
        String str = Build.MODEL + '-' + Build.DEVICE;
        str = str.replace(' ', '-');
        str = str.replace('_', '-');
        sb.append(str);
        sb.append('_');
        sb.append(Build.VERSION.SDK);
        sb.append('_');
        sb.append(Build.VERSION.RELEASE);
        sb.append('_');
        sb.append(vn);
        sb.append('_');
        sb.append(vc);
        return sb.toString();
    }

    /**
     * 获取手机的cpu 信息
     *
     * @return cpuInfo
     */
    public static CPUInfo getSystemCPUInfo() {
        if (systemCPUInfo != null) {
            return systemCPUInfo;
        }
        CPUInfo info = new CPUInfo();
        String cpuInfoPath = "/proc/cpuinfo";
        FileReader fr = null;
        BufferedReader bufferedReader = null;
        try {
            fr = new FileReader(cpuInfoPath);
            bufferedReader = new BufferedReader(fr);
            final String divider = ":";
            final String append = "__";
            String line = bufferedReader.readLine();
            while (line != null) {
                String item = line.trim().toLowerCase();

                if (item.startsWith(CPUInfo.PREFIX_PROCESSOR)
                        && item.indexOf(divider, CPUInfo.PREFIX_PROCESSOR.length()) != -1) {
                    if (info.processor.length() > 0) {
                        info.processor += append;
                    }
                    info.processor += item.split(divider)[1].trim();
                } else if (item.startsWith(CPUInfo.PREFIX_FEATURES)
                        && item.indexOf(divider, CPUInfo.PREFIX_FEATURES.length()) != -1) {
                    if (info.features.length() > 0) {
                        info.features += append;
                    }
                    info.features += item.split(divider)[1].trim();
                }
                line = bufferedReader.readLine();
            }

            if (bufferedReader != null) {
                bufferedReader.close();
            }

            if (fr != null) {
                fr.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        systemCPUInfo = info;
        return info;
    }

    /**
     * 获取手机联系人列表
     *
     * @param uri uri
     * @param act context
     * @return 联系人列表
     */
    public static ArrayList<String> getPhoneContacts(Uri uri, Context act) {
        ArrayList<String> list = new ArrayList<String>();
        // 得到ContentResolver对象
        ContentResolver cr = act.getContentResolver();
        // 取得电话本中开始一项的光标
        try {
            Cursor cursor = cr.query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                // 取得联系人名字
                int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                list.add(cursor.getString(nameFieldColumnIndex));

                // 取得电话号码
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
                cursor.close();

                if (phone != null && phone.getCount() > 0) {
                    phone.moveToFirst();
                    do {
                        String str = StringUtils.formatPhoneNumber(phone.getString(phone
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                        if (!TextUtils.isEmpty(str)) {
                            list.add(str);
                        }
                    } while (phone.moveToNext());
                    if (list.size() < 2) {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * CPU信息
     */
    public static class CPUInfo {
        /**
         * CPU型号（如armv5、armv6、armv7），小写格式
         */
        public String processor = "";
        /**
         * CPU特征（如neon、vfp，或者通用），小写格式
         */
        public String features = "";
        /**
         * armv
         */
        public static final String PROCESSOR_ARM_PREFIX = "armv";
        /**
         * armv5
         */
        public static final String PROCESSOR_ARMV5 = "armv5";
        /**
         * armv6
         */
        public static final String PROCESSOR_ARMV6 = "armv6";
        /**
         * armv7
         */
        public static final String PROCESSOR_ARMV7 = "armv7";

        /**
         * neon
         */
        public static final String FEATURE_NEON = "neon";
        /**
         * vfp
         */
        public static final String FEATURE_VFP = "vfp";
        /**
         * 通用特征
         */
        public static final String FEATURE_COMMON = "common";

        /**
         * 型号信息的定义前缀
         */
        private static final String PREFIX_PROCESSOR = "processor";

        /**
         * feature信息的定义前缀
         */
        private static final String PREFIX_FEATURES = "features";

        /**
         * @return cpupath
         */
        public String getCpuPath() {
            if (processor.startsWith(PROCESSOR_ARMV7)) {
                return "armeabi-v7a";
            } else if (processor.startsWith(PROCESSOR_ARM_PREFIX)) {
                return "armeabi";
            } else if (processor.equals("intel")) {
                return "x86";
            } else if (processor.equals("mips")) {
                return "mips";
            }
            return "";
        }
    }

    /**
     * 外部存储是否可用 (存在且具有读写权限)
     *
     * @return
     */
    public static boolean isExternalStorageAvailable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取手机 内部   可用空间大小
     *
     * @return
     */
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * 获取手机内部空间大小
     *
     * @return
     */
    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();//Gets the Android data directory
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();      //每个block 占字节数
        long totalBlocks = stat.getBlockCount();   //block总数
        return totalBlocks * blockSize;
    }

    /**
     * 获取手机 外部 可用空间大小
     *
     * @return
     */
    public static long getAvailableExternalMemorySize() {
        if (isExternalStorageAvailable()) {
            File path = Environment.getExternalStorageDirectory();//获取SDCard根目录
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } else {
            return -1;
        }
    }

    /**
     * 获取手机外部总空间大小
     *
     * @return
     */
    public static long getTotalExternalMemorySize() {
        if (isExternalStorageAvailable()) {
            File path = Environment.getExternalStorageDirectory(); //获取SDCard根目录
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        } else {
            return -1;
        }
    }

    public static String getLinkedWay(Context context) {

        return null;
    }


    public static String getCUID(Context context) {

        return null;
    }

    public static String getAppVersion(Context context) {
        if (context == null) {
            return null;
        }
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            String versionCode = packageInfo.versionName;
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断手机是否拥有Root权限。
     *
     * @return 有root权限返回true，否则返回false。
     */
    public boolean isRoot() {
        boolean bool = false;
        try {
            bool = new File("/system/bin/su").exists() || new File("/system/xbin/su").exists();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bool;
    }
}


