package com.fta.netty;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtils {

    public static String printMsg(String msg) {
        Date date = new Date();
        String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        return format +"  "+ msg;
    }
}
