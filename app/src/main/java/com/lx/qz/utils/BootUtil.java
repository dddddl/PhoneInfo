package com.lx.qz.utils;

import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import java.io.*;

public class BootUtil {

    public static int getBootTime() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            BufferedReader bufferedReader = null;
            FileReader fileReader = null;
            String btime = null;
            try {
                fileReader = new FileReader("/proc/stat");
                bufferedReader = new BufferedReader(fileReader, 1024);
                while ((btime = bufferedReader.readLine()) != null) {
                    if (btime.contains("btime")) {
                        break;
                    }
                }
                assert btime != null;
                btime = btime.replace("btime", "").trim();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                    if (fileReader != null) {
                        fileReader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return btime == null ? 0 : (int) Long.parseLong(btime);
        } else {
            return (int)(System.currentTimeMillis() - SystemClock.elapsedRealtimeNanos() / 1000000);
        }
    }
}
