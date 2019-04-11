package com.lx.qz.utils;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogHelper {

    public static volatile LogHelper m_instance = null;
    String m_filename;
    String SDPATH;

    protected LogHelper() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
        m_filename = df.format(new Date());// new Date()为获取当前系统时间
        // 以第一次启动的日期做为文件名，如果没有则创建，否则追加
//        SDPATH = Environment.getExternalStorageDirectory().getAbsolutePath();
        SDPATH = "/sdcard";
        File navifolder = new File(SDPATH + "/logs");
        if (!navifolder.exists()) {
            navifolder.mkdirs();
        }

        m_filename = SDPATH + "/logs/" + m_filename + ".txt";
        FileUtils.createFolder(m_filename);
        File file = new File(m_filename);
        if (!file.exists()) {
            try {
                file.createNewFile(); // 创建文件
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("tag", "create file failed");
            }
        }
    }

    public static synchronized LogHelper getInstance() {
        if (m_instance == null) {
            synchronized (LogHelper.class) {
                if (m_instance == null) {
                    m_instance = new LogHelper();
                }
            }
        }
        return m_instance;
    }

    public void saveLog(String TAG, String logstr) {
        m_instance.saveLogToFile(TAG, logstr);
    }

    private void saveLogToFile(String TAG, String logstr) {
        // 启动线程在文件末尾追加log
        new LogThread(m_filename, TAG + logstr).start();
    }

    class LogThread extends Thread {
        private String m_logfilename;
        private String m_logstr;

        public LogThread(String name, String logstr) {
            m_logfilename = name;

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");// 设置日期格式
            String t_time = df.format(new Date());

            m_logstr = t_time + logstr + " \n"; // 需要换行
        }

        @Override
        public void run() {
            LogWriter logwter = new LogWriter();
            logwter.writeToFile(m_logfilename, m_logstr);
            Log.e("qz", m_logstr);
        }
    }

    ;

    public static class LogWriter {
        public static synchronized void writeToFile(String filename,
                                                    String logstr) {
            try {
                FileOutputStream fos = new FileOutputStream(new File(filename),
                        true);
                fos.write(logstr.getBytes());
                fos.close();
            } catch (IOException e) {
                Log.e("qz", "create file failed recreate");
                m_instance = null;
            }
        }
    }

    ;

}